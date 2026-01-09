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
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuyStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.ClosedOfferException;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.mapper.BuyerOrderMapper;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.PartOfferToBuyRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.BuyerOrderSpecification;
import org.gafiev.peertopeerbazaar.repository.specification.DeliverySpecification;
import org.gafiev.peertopeerbazaar.service.model.interfaces.BuyerOrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BuyerOrderServiceImpl implements BuyerOrderService {
    private final BuyerOrderRepository buyerOrderRepository;
    private final BuyerOrderMapper buyerOrderMapper;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final PartOfferToBuyRepository partOfferToBuyRepository;

    @Override
    public BuyerOrderResponse getByIdWithBuyer(Long buyerOrderId) {
        BuyerOrder buyerOrder = buyerOrderRepository.findByIdWithBuyer(buyerOrderId)
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId))));
        return buyerOrderMapper.toBuyerOrderResponse(buyerOrder);
    }

    @Override
    public BuyerOrderResponse get(Long buyerOrderId, Long buyerId) {
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
    public Set<BuyerOrderResponse> getAllByStatus(Long buyerId, BuyerOrderStatus buyerOrderStatus) {
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

        Set<PartOfferToBuy> parts = basket.getPartOfferToBuySet().stream()
                .filter(part -> partsToOrder.partOfferToBuyIds().contains(part.getId()))
                .collect(Collectors.toSet());

        if (partsToOrder.partOfferToBuyIds().size() != parts.size()) {
            Set<Long> partIds = parts.stream().map(PartOfferToBuy::getId).collect(Collectors.toSet());
            Set<Long> absentIds = partsToOrder.partOfferToBuyIds().stream()
                    .filter(id -> !partIds.contains(id)).collect(Collectors.toSet());

            throw new EntityNotFoundException("PartOfferToBuyIds %s are not parts of basket with id = %d".formatted(absentIds, buyerId));
        }

        Set<PartOfferToBuy> closedOfferParts = parts.stream()
                .filter(partOfferToBuy -> partOfferToBuy.getSellerOffer().getOfferStatus() == OfferStatus.CLOSED)
                .collect(Collectors.toSet());

        if (!closedOfferParts.isEmpty()) {
            closedOfferParts.forEach(basket::removePartOfferToBuy);
            throw new ClosedOfferException(closedOfferParts.stream().map(PartOfferToBuy::getId).collect(Collectors.toSet()));
        }

        Map<OfferStatus, List<PartOfferToBuy>> statusToPart = parts.stream()
                .collect(Collectors.groupingBy(part -> part.getSellerOffer().getOfferStatus()));
        List<PartOfferToBuy> presaleParts = statusToPart.get(OfferStatus.PRESALE);
        boolean isPresalePresent = presaleParts != null && !presaleParts.isEmpty();
        if (isPresalePresent && statusToPart.size() > 1) {
            throw new IllegalBusinessStateException("Cannot create orders for presale and not presale status in a single request");
        }

        final Payment payment = new Payment();
        if (isPresalePresent) {
            Map<SellerOffer, List<PartOfferToBuy>> sellerOfferToParts = presaleParts.stream()
                    .collect(Collectors.groupingBy(PartOfferToBuy::getSellerOffer));

            return sellerOfferToParts.entrySet().stream()
                    .map(entry -> createOrder(Map.of(entry.getKey(), entry.getValue()), buyer, payment, basket))
                    .map(buyerOrderMapper::toBuyerOrderResponse)
                    .collect(Collectors.toSet());
        }

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
            Set<Delivery> deliveriesToRemove = buyerOrder.getDeliverySet().stream()
                    .filter(delivery -> requestNew.deliveryIdsToRemove().contains(delivery.getId()))
                    .collect(Collectors.toSet());
            deliveriesToRemove.forEach(buyerOrder::removeDelivery);
        }

        if (requestNew.partOfferToBuyIdsToRemove() != null && !requestNew.partOfferToBuyIdsToRemove().isEmpty()) {
            Set<PartOfferToBuy> partsToRemove = buyerOrder.getPartOfferToBuySet().stream()
                    .filter(partOfferToBuy -> requestNew.partOfferToBuyIdsToRemove().contains(partOfferToBuy.getId()))
                    .collect(Collectors.toSet());
            partsToRemove.forEach(buyerOrder::removePartOfferToBuy);
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
    public void delete(Long buyerOrderId) {
        buyerOrderRepository.deleteById(buyerOrderId);
    }

    /**
     * Creates BuyerOrder from buyer, payment, basket and Map of PartOfferToBuy grouped by SellerOffer.
     * Updates PartOfferToBuy statuses, closes SellerOffer if needed, and saves order.
     *
     * @param sellerOfferToOrderParts Map<SellerOffer, List<PartOfferToBuy>> for grouping PartOfferToBuy by SellerOffer.
     * @param buyer                   User entity.
     * @param payment                 Payment (nullable, new one created if null).
     * @param basket                  Basket entity.
     * @return BuyerOrder entity.
     * @throws IllegalBusinessStateException if not enough parts available for order.
     */
    private BuyerOrder createOrder(Map<SellerOffer, List<PartOfferToBuy>> sellerOfferToOrderParts, User buyer, @Nullable Payment payment, Basket basket) {
        Map<SellerOffer, Set<PartOfferToBuy>> updatedSellerOfferToOrderParts = sellerOfferToOrderParts.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getKey().getPartOfferToBuyList().stream()
                        .filter(p -> p.getStatus().equals(PartOfferToBuyStatus.NOT_RESERVED))
                        .limit(entry.getValue().size()) // чей размер? мой размер из риквеста
                        .collect(Collectors.toSet())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        updatedSellerOfferToOrderParts.forEach((offer, updatedOrderParts) -> {
            List<PartOfferToBuy> partsFromBasket = Objects.requireNonNullElse(sellerOfferToOrderParts.get(offer), List.of());
            if (updatedOrderParts.size() < partsFromBasket.size()) {
                throw new IllegalBusinessStateException("Not enough partsFromOffer for order : sellerOfferId = : %d"
                        .formatted(offer.getId()));
            }

            partsFromBasket.forEach(p -> {
                if (!updatedOrderParts.contains(p)) {
                    basket.removePartOfferToBuy(p);
                }
            });

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
        }

        payment = Objects.requireNonNullElse(payment, new Payment());

        payment.setPaymentStatus(PaymentStatus.CREATED);
        payment.setPaymentMode(PaymentMode.BANK_TRANSFER);
        payment.setCurrency(CurrencyCode.RUB);

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
     * Key in Map<SellerAndAddress, List<PartOfferToBuy>> for grouping PartOfferToBuy by seller and address.
     *
     * @param sellerId Unique seller (User) identifier.
     * @param address  Seller Address entity.
     */
    private record SellerAndAddress(Long sellerId, Address address) {
    }
}
