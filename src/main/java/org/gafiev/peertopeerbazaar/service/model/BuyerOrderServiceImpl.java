package org.gafiev.peertopeerbazaar.service.model;

import com.neovisionaries.i18n.CurrencyCode;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.order.*;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.ClosedOfferException;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.mapper.BuyerOrderMapper;
import org.gafiev.peertopeerbazaar.mapper.ExternalDroneMapper;
import org.gafiev.peertopeerbazaar.mapper.TimeSlotMapper;
import org.gafiev.peertopeerbazaar.repository.*;
import org.gafiev.peertopeerbazaar.repository.specification.BuyerOrderSpecification;
import org.gafiev.peertopeerbazaar.repository.specification.DeliverySpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.BuyerOrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BuyerOrderServiceImpl implements BuyerOrderService {
    private final BuyerOrderRepository buyerOrderRepository;
    private final BuyerOrderMapper buyerOrderMapper;
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final SellerOfferRepository sellerOfferRepository;
    private final ExternalDroneService externalDroneService;
    private final DroneRepository droneRepository;
    private final ExternalDroneMapper externalDroneMapper;
    private final DeliveryRepository deliveryRepository;
    private final PartOfferToBuyRepository partOfferToBuyRepository;
    private final TimeSlotMapper timeSlotMapper;
    private final PaymentRepository paymentRepository;


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
    @Transactional
    public Set<BuyerOrderResponse> getAll(Long buyerId, BuyerOrderStatus buyerOrderStatus) {
        User buyer = userRepository.findByIdWithBuyerOrdersAndSellerOffers(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        Set<BuyerOrder> buyerOrderSet = buyer.getBuyerOrderSet().stream()
                .filter(order -> order.getBuyerOrderStatus().equals(buyerOrderStatus)).collect(Collectors.toSet());
        return buyerOrderMapper.toBuyerOrderResponseSet(buyerOrderSet);
    }

    @Override
    @Transactional
    public Set<BuyerOrderResponse> getAllBuyerOrders(BuyerOrderFilterRequest filterRequest) {
        List<BuyerOrder> buyerOrderList = buyerOrderRepository.findAll(BuyerOrderSpecification.filterByParams(filterRequest));
        Set<BuyerOrder> buyerOrderSet = new HashSet<>(buyerOrderList);
        return buyerOrderMapper.toBuyerOrderResponseSet(buyerOrderSet);
    }


    @Override
    @Transactional
    public Set<BuyerOrderResponse> create(Long buyerId, BuyerOrderCreateRequest partsToOrder) {
        User buyer = userRepository.findByIdWithBasket(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(buyerId))));
        Basket basket = buyer.getBasket();

        // Не используется !!!!!!
        Set<SellerOffer> sellerOffers = basket.getPartOfferToBuySet().stream()
                .map(PartOfferToBuy::getSellerOffer)
                .collect(Collectors.toSet());


        // собираем множество, которое есть пересечение корзины пользователя и partsToOrder
        Set<PartOfferToBuy> parts = basket.getPartOfferToBuySet().stream()
                .filter(part -> partsToOrder.partOfferToBuyIds().contains(part.getId()))
                .collect(Collectors.toSet());

        if (partsToOrder.partOfferToBuyIds().size() != parts.size()) {
            Set<Long> partIds = parts.stream().map(PartOfferToBuy::getId).collect(Collectors.toSet()); // Ids пересечения
            Set<Long> absentIds = partsToOrder.partOfferToBuyIds().stream().filter(id -> !partIds.contains(id)).collect(Collectors.toSet()); // симметрическая разность partsToOrder и корзины

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
            Map<SellerOffer, List<PartOfferToBuy>> sellerOfferToParts = presaleParts.stream()
                    .collect(Collectors.groupingBy(PartOfferToBuy::getSellerOffer));// TODO создавать отдельную доставку для каждого сочетания продавец+адрес, логика в доставке

            return sellerOfferToParts.entrySet().stream()
                    .map(entry -> createOrder(Map.of(entry.getKey(),entry.getValue()), buyer, payment, basket))
                    .map(buyerOrderMapper::toBuyerOrderResponse)
                    .collect(Collectors.toSet());
        }

        // здесь все части в статусе Opened. Оформляем для них по одному заказу на все части одного поставщика забираемые с одного адреса
        Map<SellerAndAddress, List<PartOfferToBuy>> sellerIdAndAddressToParts = parts.stream()
                .collect(Collectors.groupingBy(part -> new SellerAndAddress(part.getSellerOffer().getSeller().getId(), part.getSellerOffer().getAddress())));

        List<Map<SellerOffer, List<PartOfferToBuy>>> sellerOfferToOrderPartsList = sellerIdAndAddressToParts.values().stream()
                .map(partOfferToBuys -> partOfferToBuys.stream()
                        .collect(Collectors.groupingBy(PartOfferToBuy::getSellerOffer)))
                .toList();

        return sellerOfferToOrderPartsList.stream()
                .map(sellerOfferToOrderParts -> createOrder(sellerOfferToOrderParts, buyer, null, basket))
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
        int decreaseRating = buyer.getRatingBuyer() - 1;
        buyer.setRatingBuyer(decreaseRating);
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
     * создание заказа по частям предложений одного поставщика, забираемых с одного адреса.
     *
     * @param sellerOfferToOrderParts хэш таблица, где ключ это оффер, значение части, которые покупатель желает заказать, т.е. содержимое корзины.
     * @param buyer                   покупатель
     * @param payment                 предоплата или оплата
     * @param basket                  корзина покупателя
     * @return заказ покупателя
     */
    private BuyerOrder createOrder(Map<SellerOffer, List<PartOfferToBuy>> sellerOfferToOrderParts, User buyer, @Nullable Payment payment, Basket basket) {
        // выборка из корзины ???
        Map<SellerOffer, Set<PartOfferToBuy>> updatedSellerOfferToOrderParts = sellerOfferToOrderParts.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getKey().getPartOfferToBuyList().stream()
                        .filter(p -> p.getStatus().equals(PartOfferToBuyStatus.NOT_RESERVED))
                        .limit(entry.getValue().size()) // чей размер?
                        .collect(Collectors.toSet())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        updatedSellerOfferToOrderParts.forEach((offer, updatedOrderParts) -> {
            List<PartOfferToBuy> partsFromBasket = Objects.requireNonNullElse(sellerOfferToOrderParts.get(offer), List.of());
            if (updatedOrderParts.size() < partsFromBasket.size()) {
                throw new IllegalBusinessStateException("Not enough partsFromOffer for order : sellerOfferId = : %d"
                        .formatted(offer.getId()));
            }
            // удаление из корзины частей, которых нет в оффере
            partsFromBasket.forEach(p -> {
                if (!updatedOrderParts.contains(p)) {
                    basket.removePartOfferToBuy(p);
                }
            });
            // добавление в корзину частей из оффера
            updatedOrderParts.forEach(p -> {
                if (!partsFromBasket.contains(p)) {
                    basket.addPartOfferToBuy(p);
                }
            });
        });

        BuyerOrder buyerOrder = new BuyerOrder();
        buyerOrder.setBuyer(buyer);
        buyerOrder.setBuyerOrderStatus(BuyerOrderStatus.CREATED);

        for (Map.Entry<SellerOffer, Set<PartOfferToBuy>> entry : updatedSellerOfferToOrderParts.entrySet()) {
            entry.getValue().forEach(part -> {
                buyerOrder.addPartOfferToBuy(part);
                part.setStatus(PartOfferToBuyStatus.RESERVED);
                basket.removePartOfferToBuy(part);
            });

            SellerOffer offer = entry.getKey();
            if (offer.getActualUnitCount() <= 0) {
                offer.setOfferStatus(OfferStatus.CLOSED);
                log.info("SellerOffer ID {} is now closed.", offer.getId());
            }
        }

        payment = Objects.requireNonNullElse(payment, new Payment());

        payment.setPaymentStatus(PaymentStatus.CREATED);
        payment.setPaymentMode(PaymentMode.BANK_TRANSFER);
        payment.setCurrency(CurrencyCode.RUB);

        //из мапы updatedSellerOfferToOrderParts
        BigDecimal currentOrderAmount = updatedSellerOfferToOrderParts.entrySet().stream()
                .map(entry -> entry.getKey()
                        .getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(entry.getValue().size())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paymentAmount = Objects.requireNonNullElse(payment.getAmount(), BigDecimal.ZERO).add(currentOrderAmount);
        payment.setAmount(paymentAmount);

        payment.addBuyerOrder(buyerOrder);

        return buyerOrderRepository.save(buyerOrder);

    }

    /**
     * ключ для мапы offerIdAndAddressToParts
     *
     * @param sellerId
     * @param address
     */
    private record SellerAndAddress(Long sellerId, Address address) {
    }

}
