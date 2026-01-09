package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryUpdateTime;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.exception.DroneException;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.exception.PaymentStatusException;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.mapper.DroneMapper;
import org.gafiev.peertopeerbazaar.mapper.TimeSlotMapper;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.DroneRepository;
import org.gafiev.peertopeerbazaar.repository.specification.DeliverySpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    // Renamed for clarity: we use these statuses to restrict further changes.
    private static final Set<DeliveryStatus> FINAL_DELIVERY_STATUSES = Set.of(
            DeliveryStatus.CANCELLED_BY_BUYER,
            DeliveryStatus.CANCELLED_BY_SELLER,
            DeliveryStatus.FAILED,
            DeliveryStatus.DELIVERED);
    private final DeliveryRepository deliveryRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final DeliveryMapper deliveryMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final ExternalDroneService externalDroneService;
    private final DroneServiceImpl droneService;
    private final DroneMapper droneMapper;
    private final AddressRepository addressRepository;
    private final DroneRepository droneRepository;


    @Override
    public DeliveryResponse getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    @Transactional
    public Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(Long buyerOrderId) {
        BuyerOrder buyerOrder = buyerOrderRepository.findByIdWithDelivery(buyerOrderId)
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId))));
        return deliveryMapper.toDeliveryResponseSet(buyerOrder.getDeliverySet());
    }

    @Override
    public Set<DeliveryResponse> getAllDeliveriesByFilter(DeliveryFilterRequest filterRequest) {
        // We use a Set to ensure uniqueness if the specification joins produce duplicates
        List<Delivery> deliveryList = deliveryRepository.findAll(DeliverySpecification.filterByParams(filterRequest));
        return deliveryMapper.toDeliveryResponseSet(new HashSet<>(deliveryList));
    }

    @Override
    public DeliveryResponse create(DeliveryCreateRequest request) {
        BuyerOrder buyerOrder = buyerOrderRepository.findByIdWithPartOfferToBuyAndWithSellerOfferWithAddressAndDeliverySet(request.buyerOrderId())
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(request.buyerOrderId()))));

        // Проверяем, что список доставок пуст
        if (!buyerOrder.getDeliverySet().isEmpty()) {
            throw new IllegalBusinessStateException("Delivery for this order has already been created.");
        }

        if (buyerOrder.getPayment().getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentStatusException("Delivery is impossible. Payment is not done");
        }

        Address toAddress = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(request.addressId()))));

        // Safely extract origin address from the first part offer
        Address fromAddress = buyerOrder.getPartOfferToBuySet().stream()
                .findFirst()
                .map(part -> part.getSellerOffer().getAddress())
                .orElseThrow(() -> new IllegalBusinessStateException("Order has no associated seller offers."));

        Delivery delivery = new Delivery();
        delivery.setDeliveryStatus(DeliveryStatus.CREATED);
        delivery.setToAddress(toAddress);
        delivery.setFromAddress(fromAddress);
        buyerOrder.addDelivery(delivery);

        return deliveryMapper.toDeliveryResponse(deliveryRepository.save(delivery));
    }

    @Override
    public List<TimeSlotResponse> takeTimeSlots(Long id) {
        Delivery delivery = deliveryRepository.findDeliveryByIdWithBuyerOrderWithAddresses(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        Set<TimeSlotResponse> timeSlotResponseSet = externalDroneService.requestDroneSchedule(deliveryMapper.toDeliveryDroneRequest(delivery));

        return timeSlotResponseSet.stream().sorted(Comparator.comparing(TimeSlotResponse::start)).toList();
    }

    @Override
    @Transactional
    public DeliveryResponse assignDroneForDelivery(Long id, DeliveryUpdateTime updateTime) {
        log.info("Assigning drone to delivery ID: {}", id);
        log.debug("Update payload: {}", updateTime);

        Delivery delivery = deliveryRepository.findDeliveryByIdWithBuyerOrder(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));

        TimeSlotResponse chosenSlot = updateTime.timeSlot();
        // Business rule: The drone requires at least 30 minutes before the slot's end
        // to account for flight/approach time.
        if (chosenSlot.end().minusMinutes(30).isBefore(LocalDateTime.now())) {
            log.warn("Slot {} rejected: insufficient lead time for delivery ID {}", chosenSlot, id);
            throw new DroneException("Selected time slot is invalid due to flight time constraints.");
        }

        delivery.setTimeSlot(timeSlotMapper.toTimeSlot(chosenSlot));
        log.info("Updated time slot for delivery: {}", delivery.getTimeSlot());

        ExternalDroneResponse droneResponse = externalDroneService.requestDrone(deliveryMapper.toDeliveryDroneRequest(delivery));
        if (droneResponse == null || droneResponse.errorMessage() != null) {
            String error = (droneResponse == null) ? "Empty response" : droneResponse.errorMessage();
            throw new DroneException("Drone assignment failed for delivery %d. Reason: %s".formatted(id, error));
        }

        // 2. ИЩЕМ дрон в своей базе по внешнему ID, чтобы не создавать дубликат
        Drone drone = droneRepository.findByDroneServiceId(droneResponse.droneServiceId())
                .map(existingDrone -> {
                    // Если нашли — просто обновляем статус, если он изменился
                    existingDrone.setDroneStatus(droneResponse.droneStatus());
                    return existingDrone;
                })
                .orElseGet(() -> droneMapper.toDrone(droneResponse));// Если нет — мапим новый

        drone.addDelivery(delivery);

        delivery.setDeliveryStatus(DeliveryStatus.DRONE_ASSIGNED);

        // 4. Сохраняем дрон (он либо обновится, либо создастся один раз)
        droneRepository.save(drone);
        return deliveryMapper.toDeliveryResponse(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public DeliveryResponse updateStatus(Long id, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findByIdWithBuyerAndSellerAndDroneAndPayment(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));

        if (FINAL_DELIVERY_STATUSES.contains(delivery.getDeliveryStatus())) {
            throw new IllegalBusinessStateException("Cannot update status. Delivery is already in a final state: " + delivery.getDeliveryStatus());
        }

        switch (newStatus) {
            case CANCELLED_BY_BUYER -> decreaseBuyerRating(delivery);
            case CANCELLED_BY_SELLER -> decreaseSellerRating(delivery);
            default -> throw new IllegalBusinessStateException("Unsupported status update: " + newStatus);
        }

        delivery.setDeliveryStatus(newStatus);
        log.info("Delivery ID {} status updated to {}", id, newStatus);

        return deliveryMapper.toDeliveryResponse(deliveryRepository.save(delivery));
    }


    private void decreaseBuyerRating(Delivery delivery) {
        var buyer = delivery.getBuyerOrder().getBuyer();
        int current = (buyer.getRatingBuyer() != null) ? buyer.getRatingBuyer() : 0;
        buyer.setRatingBuyer(Math.max(0, current - 1));
    }

    private void decreaseSellerRating(Delivery delivery) {
        // Safely navigate to seller rating
        delivery.getBuyerOrder().getPartOfferToBuySet().stream()
                .findFirst()
                .map(part -> part.getSellerOffer().getSeller())
                .ifPresent(seller -> {
                    int current = (seller.getRatingSeller() != null) ? seller.getRatingSeller() : 0;
                    seller.setRatingSeller(Math.max(0, current - 1));
                });
    }

    @Override
    public void deleteDelivery(Long id) {
        if (!deliveryRepository.existsById(id)) {
            throw new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id)));
        }
        deliveryRepository.deleteById(id);
        log.info("Delivery ID {} deleted permanently.", id);
    }
}

