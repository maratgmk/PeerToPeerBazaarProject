package org.gafiev.peertopeerbazaar.service;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.order.*;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.ClosedOfferException;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.mapper.BuyerOrderMapper;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.service.interfaces.BuyerOrderService;
import org.gafiev.peertopeerbazaar.service.interfaces.DroneService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BuyerOrderServiceImpl implements BuyerOrderService {
    private final BuyerOrderRepository buyerOrderRepository;
    private final BuyerOrderMapper buyerOrderMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final DroneService droneService;


    /**
     * метод создания нового заказа покупателя
     *
     * @param buyerId   id покупателя
     * @param candidate риквест запрос от покупателя
     * @return множество заказов разделённых по поставщикам, адресам забора товаров дронами, временем забора товаров
     */
    @Override
    @Transactional
    public Set<BuyerOrderResponse> create(Long buyerId, BuyerOrderCreateRequest candidate) {
        User buyer = userRepository.findByIdWithBasket(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("buyerId", String.valueOf(buyerId))));
        Basket basket = buyer.getBasket();

        // проверяем, что все Id частей присутствуют в корзине пользователя
        Set<PartOfferToBuy> parts = basket.getPartOfferToBuySet().stream()
                .filter(part -> candidate.partOfferToBuyIds().contains(part.getId()))
                .collect(Collectors.toSet());

        if (candidate.partOfferToBuyIds().size() != parts.size()) {
            Set<Long> partIds = parts.stream().map(PartOfferToBuy::getId).collect(Collectors.toSet()); // Ids корзины равные Ids кандидата
            Set<Long> absentIds = candidate.partOfferToBuyIds().stream().filter(id -> !partIds.contains(id)).collect(Collectors.toSet()); // Ids кандидата, которых нет в корзине

            throw new EntityNotFoundException("PartOfferToBuyIds %s are not parts of basket with id = %d".formatted(absentIds, buyerId));
        }

        // проверяем,что в запросе нет частей закрытых офферов
        Set<PartOfferToBuy> closedOfferParts = parts.stream()
                .filter(partOfferToBuy -> partOfferToBuy.getSellerOffer().getOfferStatus() == OfferStatus.CLOSED)
                .collect(Collectors.toSet());

        if (!closedOfferParts.isEmpty()) {
            closedOfferParts.forEach(basket::removePartOfferToBuy);
            throw new ClosedOfferException(closedOfferParts.stream().map(PartOfferToBuy::getId).collect(Collectors.toSet()));
        }

        // проверяем есть ли в заказе части оффера в статусе PreSale и в статусе Opened
        // если присутствуют части с обоими статусами бросаем исключение
        Map<OfferStatus, List<PartOfferToBuy>> statusToPart = parts.stream()
                .collect(Collectors.groupingBy(part -> part.getSellerOffer().getOfferStatus()));
        List<PartOfferToBuy> presaleParts = statusToPart.get(OfferStatus.PRESALE);
        boolean isPresalePresent = presaleParts != null && !presaleParts.isEmpty();
        if (isPresalePresent && statusToPart.size() > 1) {
            throw new IllegalBusinessStateException("Cannot create orders for presale and not presale status in a single request");
        }

        // если все части Presale, то оформляем для них заказы с одной общей оплатой
        final Payment payment = new Payment();
        if (isPresalePresent) {
            // заводим заказ на каждый оффер, потому что Presale у офферов заканчивается в разное время
            Map<Long, List<PartOfferToBuy>> offerToPart = presaleParts.stream().collect(Collectors.groupingBy(part -> part.getSellerOffer().getId()));
            return offerToPart.values().stream()
                    .map(partList -> createOrder(new HashSet<>(partList), buyer, null, payment))
                    .map(buyerOrderMapper::toBuyerOrderResponse)
                    .collect(Collectors.toSet());
        }

        // здесь все части в статусе Opened. Оформляем для них по одному заказу на все части одного поставщика забираемые с одного адреса
        Map<SellerOfferIdAndAddress, List<PartOfferToBuy>> offerIdAndAddressToParts = parts.stream()
                .collect(Collectors.groupingBy(part -> new SellerOfferIdAndAddress(part.getSellerOffer().getSeller().getId(), part.getSellerOffer().getAddress())));

        return offerIdAndAddressToParts.values().stream()
                .map(partList -> createOrder(new HashSet<>(partList), buyer, candidate.delivery(), payment))
                .map(buyerOrderMapper::toBuyerOrderResponse)
                .collect(Collectors.toSet());
    }

    /**
     * метод получения DTO заказа покупателя
     *
     * @param buyerId      id покупателя
     * @param buyerOrderId id заказа покупателя
     * @return buyerOrderResponse
     */
    @Override
    public BuyerOrderResponse get(Long buyerId, Long buyerOrderId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("buyerId", String.valueOf(buyerId))));
        BuyerOrder buyerOrder = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getId().equals(buyerOrderId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("buyerOrderId", String.valueOf(buyerOrderId))));
        return buyerOrderMapper.toBuyerOrderResponse(buyerOrder);
    }

    /**
     * метод получения множества DTO заказов покупателя, соответствующих определённому статусу
     *
     * @param buyerId          id покупателя
     * @param buyerOrderStatus состояние заказа покупателя
     * @return buyerOrderResponseSet
     */
    @Override
    public Set<BuyerOrderResponse> getAll(Long buyerId, BuyerOrderStatus buyerOrderStatus) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("buyerId", String.valueOf(buyerId))));
        Set<BuyerOrder> buyerOrderSet = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getBuyerOrderStatus().equals(buyerOrderStatus)).collect(Collectors.toSet());
        return buyerOrderMapper.toBuyerOrderResponseSet(buyerOrderSet);
    }

    /**
     * метод отмены заказа покупателя
     *
     * @param buyerId      id покупателя
     * @param buyerOrderId id заказа покупателя
     */
    @Override
    @Transactional
    public void cancel(Long buyerId, Long buyerOrderId) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("buyerId", String.valueOf(buyerId))));
        BuyerOrder buyerOrder = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getId().equals(buyerOrderId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("buyerOrderId", String.valueOf(buyerOrderId))));
        buyerOrder.setBuyerOrderStatus(BuyerOrderStatus.DENIED);
        userRepository.save(buyer);
    }

    /**
     * метод удаления заказа покупателя
     *
     * @param buyerId      id покупателя
     * @param buyerOrderId id заказа покупателя
     */
    @Override
    @Transactional
    public void delete(Long buyerId, Long buyerOrderId) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("buyerId", String.valueOf(buyerId))));
        BuyerOrder buyerOrder = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getId().equals(buyerOrderId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("buyerOrderId", String.valueOf(buyerOrderId))));
        buyer.removeBuyerOrder(buyerOrder);
        userRepository.save(buyer);
    }

    /**
     * создание заказа по частям предложений одного поставщика, забираемых с одного адреса
     *
     * @param parts           части корзины? части ?
     * @param buyer           покупатель
     * @param deliveryRequest DTO поставки (куда, что, когда)
     * @return заказ покупателя
     */
    private BuyerOrder createOrder(Set<PartOfferToBuy> parts, User buyer, @Nullable DeliveryCreateRequest deliveryRequest, @Nullable Payment payment) {
        if (parts.isEmpty()) throw new IllegalBusinessStateException("Order cannot be empty");
        Delivery delivery = null;
        if (deliveryRequest != null) {
            delivery = new Delivery();
            delivery.setDeliveryStatus(DeliveryStatus.CREATED);
            delivery.setExpectedDateTime(deliveryRequest.expectedDateTime());
            Drone drone = droneService.requestDrone(delivery)
                    .orElseThrow(() -> new IllegalBusinessStateException("No drone for your request"));

            drone.addDelivery(delivery);
            Address toAddress = addressRepository.findById(deliveryRequest.addressId())
                    .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("addressId", String.valueOf(deliveryRequest.addressId()))));
            Address fromAddress = parts.stream().findFirst().orElseThrow().getSellerOffer().getAddress();
            delivery.setToAddress(toAddress);
            delivery.setFromAddress(fromAddress);
        }

        BuyerOrder buyerOrder = new BuyerOrder();
        buyerOrder.setBuyer(buyer);
        buyerOrder.setBuyerOrderStatus(BuyerOrderStatus.CREATED);
        if (delivery != null) {
            buyerOrder.addDelivery(delivery);
        }

        for (PartOfferToBuy part : parts) {
            if (part.getSellerOffer().getActualUnitCount() < part.getUnitCount())
                throw new IllegalBusinessStateException("Cannot create order with %d unit: actual amount is less".formatted(part.getUnitCount()));
            buyerOrder.addPartOfferToBuy(part);
        }
        payment = Objects.requireNonNullElse(payment, new Payment());
        payment.addBuyerOrder(buyerOrder);
        payment.setPaymentStatus(PaymentStatus.CREATED);
        payment.setPaymentMode(PaymentMode.BANK_TRANSFER);
        //
        BigDecimal currentOrderAmount = parts.stream()
                .map(part -> part.getSellerOffer()
                        .getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(part.getUnitCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paymentAmount = Objects.requireNonNullElse(payment.getAmount(), BigDecimal.ZERO).add(currentOrderAmount);
        payment.setAmount(paymentAmount);
        return buyerOrderRepository.save(buyerOrder);
    }

    /**
     * ключ для мапы
     *
     * @param sellerOfferId
     * @param address
     */
    private record SellerOfferIdAndAddress(Long sellerOfferId, Address address) {

    }
}
