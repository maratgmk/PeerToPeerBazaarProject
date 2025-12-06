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
    private static final Set<DeliveryStatus> FINAL_DELIVERY_STATUSES = Set.of(DeliveryStatus.CANCELLED_BY_BUYER,
            DeliveryStatus.CANCELLED_BY_SELLER, DeliveryStatus.FAILED, DeliveryStatus.DELIVERED);
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
        List<Delivery> deliveryList = deliveryRepository.findAll(DeliverySpecification.filterByParams(filterRequest));
        Set<Delivery> deliverySet = new HashSet<>(deliveryList);
        return deliveryMapper.toDeliveryResponseSet(deliverySet);
    }

    @Override
    public DeliveryResponse create(DeliveryCreateRequest request) {
        BuyerOrder buyerOrder = buyerOrderRepository.findByIdWithPartOfferToBuyAndWithSellerOfferWithAddress(request.buyerOrderId())
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(request.buyerOrderId()))));
        if (buyerOrder.getPayment().getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentStatusException("Delivery is impossible. Payment is not done");
        }

        Delivery delivery = new Delivery();
        delivery.setDeliveryStatus(DeliveryStatus.CREATED);

        Address toAddress = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(request.addressId()))));

        Address fromAddress = buyerOrder.getPartOfferToBuySet().stream().findFirst().orElseThrow().getSellerOffer().getAddress();
        delivery.setToAddress(toAddress);
        delivery.setFromAddress(fromAddress);
        buyerOrder.addDelivery(delivery);

        delivery = deliveryRepository.save(delivery);

        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public List<TimeSlotResponse> takeTimeSlots(Long id) {
        Delivery delivery = deliveryRepository.findDeliveryByIdWithBuyerOrderWithAddresses(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        Set<TimeSlotResponse> timeSlotResponseSet = externalDroneService.requestDroneSchedule(deliveryMapper.toDeliveryDroneRequest(delivery));

        return timeSlotResponseSet.stream().sorted(Comparator.comparing(TimeSlotResponse::start)).toList();
    }

    /**
     * Установка требуемого временного интервала для существующей (созданной) доставки.
     */
    @Override
    @Transactional
    public DeliveryResponse assignDroneForDelivery(Long id, DeliveryUpdateTime updateTime) {
        log.info("Starting assign drone for delivery ID: {}", id);
        log.info("Update time data: {}", updateTime);

        Delivery delivery = deliveryRepository.findDeliveryByIdWithBuyerOrder(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));

        log.info("Found delivery: {}", delivery);

        TimeSlotResponse timeSlotResponse = updateTime.timeSlot();
        if (timeSlotResponse.end().minusMinutes(30).isBefore(LocalDateTime.now())) {
            timeSlotResponse = null;
            log.info("Выбранный timeSlot с поправкой на время подлета : {} находится уже в прошлом", updateTime.timeSlot());
        }
        if (timeSlotResponse == null) {
            throw new DroneException("Cannot request a drone for delivery, unknown time delivery");
        }

        delivery.setTimeSlot(timeSlotMapper.toTimeSlot(timeSlotResponse));
        log.info("Updated time slot for delivery: {}", delivery.getTimeSlot());

        log.info("Sending to drone service: {}", deliveryMapper.toDeliveryDroneRequest(delivery));
        ExternalDroneResponse droneResponse = externalDroneService.requestDrone(deliveryMapper.toDeliveryDroneRequest(delivery));
        if (droneResponse == null || droneResponse.errorMessage() != null) {
            throw new DroneException("Cannot request a drone for delivery : deliveryId = %s, reason = %s"
                    .formatted(id, droneResponse == null ? "response is null" : droneResponse.errorMessage()));
        }

        Drone drone = droneMapper.toDrone(droneResponse);
        drone.addDelivery(delivery);

        delivery.setDeliveryStatus(DeliveryStatus.DRONE_ASSIGNED);

        drone = droneRepository.save(drone);

        log.info("Saved drone: {}", drone);

        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    /**
     * изменение статуса доставки на отмену и уменьшение рейтингов покупателю и продавцу.
     *
     * @param id     идентификатор поставки
     * @param status отмены доставки
     * @return DTO доставки с обновленным статусом отмены
     */
    @Override
    @Transactional
    public DeliveryResponse updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findByIdWithBuyerAndSellerAndDroneAndPayment(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        Drone drone = delivery.getDrone();

        if (FINAL_DELIVERY_STATUSES.contains(delivery.getDeliveryStatus())) {
            throw new IllegalBusinessStateException("You can't change final status delivery. " +
                    "Actual delivery status is : " + delivery.getDeliveryStatus());
        }

        switch (status) {
            case CANCELLED_BY_BUYER -> {
                int currentRatingBuyer = delivery.getBuyerOrder().getBuyer().getRatingBuyer() != null ? delivery.getBuyerOrder().getBuyer().getRatingBuyer() : 0;
                delivery.getBuyerOrder().getBuyer().setRatingBuyer(currentRatingBuyer - 1);
            }
            case CANCELLED_BY_SELLER -> {
                Integer ratingSeller = delivery.getBuyerOrder().getPartOfferToBuySet().stream()
                        .findFirst().orElseThrow().getSellerOffer().getSeller().getRatingSeller();
                int currentRatingSeller = ratingSeller != null ? ratingSeller : 0;
                delivery.getBuyerOrder().getBuyer().setRatingBuyer(currentRatingSeller - 1);
            }
            default -> throw new IllegalBusinessStateException("Illegal status to update delivery. " +
                    "Requested delivery status is : " + status);
        }

        droneService.cancelDrone(drone.getId(), delivery.getId());
        delivery.setDeliveryStatus(status);

        delivery = deliveryRepository.save(delivery);

        return deliveryMapper.toDeliveryResponse(delivery);
    }


    @Override
    public void deleteDelivery(Long id) {
        deliveryRepository.deleteById(id);
    }
}
