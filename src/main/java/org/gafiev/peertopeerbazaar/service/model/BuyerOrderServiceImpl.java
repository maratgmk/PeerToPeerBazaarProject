package org.gafiev.peertopeerbazaar.service.model;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.*;
import org.gafiev.peertopeerbazaar.dto.api.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.order.*;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.ClosedOfferException;
import org.gafiev.peertopeerbazaar.exception.DroneException;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.mapper.BuyerOrderMapper;
import org.gafiev.peertopeerbazaar.mapper.ExternalDroneMapper;
import org.gafiev.peertopeerbazaar.repository.*;
import org.gafiev.peertopeerbazaar.repository.specification.BuyerOrderSpecification;
import org.gafiev.peertopeerbazaar.repository.specification.DeliverySpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.BuyerOrderService;
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
    private final ExternalDroneService externalDroneService;
    private final DroneRepository droneRepository;
    private final ExternalDroneMapper externalDroneMapper;
    private final DeliveryRepository deliveryRepository;
    private final PartOfferToBuyRepository partOfferToBuyRepository;


    @Override
    public BuyerOrderResponse get(Long buyerId, Long buyerOrderId) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        BuyerOrder buyerOrder = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getId().equals(buyerOrderId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId))));
        return buyerOrderMapper.toBuyerOrderResponse(buyerOrder);
    }


    @Override
    public Set<BuyerOrderResponse> getAll(Long buyerId, BuyerOrderStatus buyerOrderStatus) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        Set<BuyerOrder> buyerOrderSet = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getBuyerOrderStatus().equals(buyerOrderStatus)).collect(Collectors.toSet());
        return buyerOrderMapper.toBuyerOrderResponseSet(buyerOrderSet);
    }

    @Override
    public Set<BuyerOrderResponse> getAllBuyerOrders(BuyerOrderFilterRequest filterRequest) {
        List<BuyerOrder> buyerOrderList = buyerOrderRepository.findAll(BuyerOrderSpecification.filterByParams(filterRequest));
        Set<BuyerOrder> buyerOrderSet = new HashSet<>(buyerOrderList);
        return buyerOrderMapper.toBuyerOrderResponseSet(buyerOrderSet);
    }


    @Override
    @Transactional
    public Set<BuyerOrderResponse> create(Long buyerId, BuyerOrderCreateRequest candidate) {
        User buyer = userRepository.findByIdWithBasket(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        Basket basket = buyer.getBasket();

        // проверяем какие части в корзине пользователя есть также и в частях кандидата
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

    @Override
    @Transactional
    public BuyerOrderResponse update(Long buyerId, Long buyerOrderId, BuyerOrderUpdateRequest requestNew) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        BuyerOrder buyerOrder = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getId().equals(buyerOrderId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId))));
        if (requestNew.deliveryIdsToRemove() != null && !requestNew.deliveryIdsToRemove().isEmpty()) {
            buyerOrder.getDeliverySet().stream()
                    .filter(delivery -> requestNew.deliveryIdsToRemove().contains(delivery.getId()))
                    .forEach(buyerOrder::removeDelivery);
        }
        if (requestNew.partOfferToBuyIdsToRemove() != null && !requestNew.partOfferToBuyIdsToRemove().isEmpty()) {
            buyerOrder.getPartOfferToBuySet().stream()
                    .filter(partOfferToBuy -> requestNew.partOfferToBuyIdsToRemove().contains(partOfferToBuy.getId()))
                    .forEach(buyerOrder::removePartOfferToBuy);
        }
        if (requestNew.deliveryIdsToAdd() != null && !requestNew.deliveryIdsToAdd().isEmpty()) {
            List<Delivery> deliveryList = deliveryRepository.findAll(DeliverySpecification.filterByParams(DeliveryFilterRequest
                    .builder()
                    .ids(requestNew.deliveryIdsToAdd())
                    .build()));
            Set<Long> presentDeliveryIds = deliveryList.stream().map(Delivery::getId).collect(Collectors.toSet());
            Set<Long> absentIds = requestNew.deliveryIdsToAdd().stream().filter(id -> !presentDeliveryIds.contains(id)).collect(Collectors.toSet());

            if (!absentIds.isEmpty()) {
                throw new EntityNotFoundException(Delivery.class, Map.of("id", absentIds.toString()));
            }
            deliveryList.forEach(buyerOrder::addDelivery);
        }
        if (requestNew.partOfferToBuyIdsToAdd() != null && !requestNew.partOfferToBuyIdsToAdd().isEmpty()) {
            List<PartOfferToBuy> parts = partOfferToBuyRepository.findAllById(requestNew.partOfferToBuyIdsToAdd());
            Set<Long> presentPartIds = parts.stream().map(PartOfferToBuy::getId).collect(Collectors.toSet());
            Set<Long> absentIds = requestNew.partOfferToBuyIdsToAdd().stream().filter(id -> !presentPartIds.contains(id)).collect(Collectors.toSet());

            if (!absentIds.isEmpty()) {
                throw new EntityNotFoundException(PartOfferToBuy.class, Map.of("id", absentIds.toString()));
            }

            parts.forEach(buyerOrder::addPartOfferToBuy);

        }
        buyerOrder = buyerOrderRepository.save(buyerOrder);
        return buyerOrderMapper.toBuyerOrderResponse(buyerOrder);
    }

    @Override
    @Transactional
    public void cancel(Long buyerId, Long buyerOrderId) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        BuyerOrder buyerOrder = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getId().equals(buyerOrderId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId))));
        buyerOrder.setBuyerOrderStatus(BuyerOrderStatus.DENIED);
        userRepository.save(buyer);
    }


    @Override
    @Transactional
    public void delete(Long buyerId, Long buyerOrderId) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        BuyerOrder buyerOrder = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getId().equals(buyerOrderId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId))));
        buyer.removeBuyerOrder(buyerOrder);
        userRepository.save(buyer);
    }

    /**
     * создание заказа по частям предложений одного поставщика, забираемых с одного адреса
     *
     * @param parts           части корзины? части ?
     * @param buyer           покупатель
     * @param deliveryRequest DTO поставки (куда, что, когда)
     * @param payment         предоплата ???
     * @return заказ покупателя
     */
    private BuyerOrder createOrder(Set<PartOfferToBuy> parts, User buyer, @Nullable DeliveryCreateRequest deliveryRequest, @Nullable Payment payment) {
        if (parts.isEmpty()) throw new IllegalBusinessStateException("Order cannot be empty");
        Delivery delivery = null;
        if (deliveryRequest != null) {
            delivery = new Delivery();
            delivery.setDeliveryStatus(DeliveryStatus.CREATED);
            delivery.setExpectedDateTime(deliveryRequest.expectedDateTime());

            Address toAddress = addressRepository.findById(deliveryRequest.addressId())
                    .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(deliveryRequest.addressId()))));
            Address fromAddress = parts.stream().findFirst().orElseThrow().getSellerOffer().getAddress();
            delivery.setToAddress(toAddress);
            delivery.setFromAddress(fromAddress);
            DeliveryDroneRequest deliveryDroneRequest = externalDroneMapper.toDeliveryDroneRequest(
                    deliveryRequest,
                    toAddress,
                    fromAddress,
                    BuyerOrder.builder().partOfferToBuySet(parts).build());
            ExternalDroneResponse droneResponse = externalDroneService.requestDrone(deliveryDroneRequest);
            if (droneResponse == null) throw new DroneException("Cannot arrange drone : response is null");
            if (droneResponse.errorMessage() != null)
                throw new DroneException("Cannot arrange drone : " + droneResponse.errorMessage());
            Drone drone = droneRepository.findByDroneServiceIdWithDeliveries(droneResponse.droneServiceId())
                    .orElseGet(() -> Drone.builder()
                            .droneServiceId(droneResponse.droneServiceId())
                            .build());
            drone.addDelivery(delivery);
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

        buyerOrder.setPayment(payment);

        return buyerOrderRepository.save(buyerOrder);
    }

    /**
     * ключ для мапы offerIdAndAddressToParts
     *
     * @param sellerOfferId
     * @param address
     */
    private record SellerOfferIdAndAddress(Long sellerOfferId, Address address) {

    }
}
