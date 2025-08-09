package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryUpdateTime;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.*;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.DroneException;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.PaymentStatusException;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.mapper.DroneMapper;
import org.gafiev.peertopeerbazaar.mapper.ExternalDroneMapper;
import org.gafiev.peertopeerbazaar.mapper.TimeSlotMapper;
import org.gafiev.peertopeerbazaar.repository.*;
import org.gafiev.peertopeerbazaar.repository.specification.DeliverySpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final UserRepository userRepository;
    private final DeliveryMapper deliveryMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final ExternalDroneService externalDroneService;
    private final DroneServiceImpl droneService;
    private final DroneMapper droneMapper;
    private final ExternalDroneMapper externalDroneMapper;
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
    public Set<DeliveryResponse> getAllDeliveries(DeliveryStatus deliveryStatus, Address toAddress, Address fromAddress, TimeSlot timeSlot) {
        Set<Delivery> deliverySet = deliveryRepository.findAllByDeliveryStatusAndFromAddressAndToAddressAndTimeSlot(deliveryStatus, toAddress, fromAddress, timeSlot);
        return deliveryMapper.toDeliveryResponseSet(deliverySet);
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
        if(buyerOrder.getPayment().getPaymentStatus() != PaymentStatus.SUCCESS){
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
     * метод на запрос нового дрона в случае неудавшейся доставки.
     * причина неудачи не известна.
     * все параметры доставки остаются прежними, запрашивается только новый дрон.
     *
     * @param failDeliveryId идентификатор неудавшейся доставки
     * @return получение Dto доставки с новым дроном
     */
    @Override
    public DeliveryResponse createDeliveryDependsOnFail(Long failDeliveryId) {
        Delivery deliveryFail = deliveryRepository.findByIdWithAddresses(failDeliveryId)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(failDeliveryId))));

        Delivery restoredDelivery = new Delivery();
        restoredDelivery.setTimeSlot(deliveryFail.getTimeSlot()); // время оставить прежнее?
        restoredDelivery.setBuyerOrder(deliveryFail.getBuyerOrder());
        restoredDelivery.setToAddress(deliveryFail.getToAddress());
        restoredDelivery.setFromAddress(deliveryFail.getFromAddress());
        restoredDelivery.setDeliveryStatus(DeliveryStatus.CREATED);

        ExternalDroneResponse droneResponse = externalDroneService.requestDrone(deliveryMapper.toDeliveryDroneRequest(restoredDelivery));

        restoredDelivery.setDrone(droneMapper.toDrone(droneResponse));
        restoredDelivery.setDeliveryStatus(DeliveryStatus.DRONE_ASSIGNED);

        restoredDelivery = deliveryRepository.save(restoredDelivery);

        return deliveryMapper.toDeliveryResponse(restoredDelivery);
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

        TimeSlotResponse timeSlot = updateTime.timeSlot();
        if(timeSlot.end().isBefore(LocalDateTime.now())){
            timeSlot = null;
            log.info("Выбранный timeSlot : {} находится уже в прошлом", updateTime.timeSlot());
        }
        if (timeSlot == null){
            throw new DroneException("Дрон назначить не возможно");
        }

        delivery.setTimeSlot(timeSlotMapper.toTimeSlot(timeSlot));
        log.info("Updated time slot for delivery: {}", delivery.getTimeSlot());

        ExternalDroneResponse droneResponse = externalDroneService.requestDrone(deliveryMapper.toDeliveryDroneRequest(delivery));
        if(droneResponse == null || droneResponse.errorMessage() != null){
            throw new DroneException("Cannot request a drone for delivery : deliveryId = %s, reason = %s"
                    .formatted(id, droneResponse == null ? "response is null" : droneResponse.errorMessage()));
        }

        Drone drone = droneMapper.toDrone(droneResponse);
        drone.addDelivery(delivery);

        delivery.setDeliveryStatus(DeliveryStatus.DRONE_ASSIGNED);

        // Сохранение объекта drone в репозитории
        drone = droneRepository.save(drone);

        // Логирование или использование обновленного объекта drone
        log.info("Saved drone: {}", drone);

        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    /**
     * обновление статуса доставки и присвоение рейтингов покупателю и продавцу.
     * @param id идентификатор поставки
     * @param status доставки
     * @return DTO доставки с обновленным статусом
     */
    @Override
    @Transactional
    public DeliveryResponse  updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findByIdWithBuyerAndSeller(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        delivery.setDeliveryStatus(status);

        int currentRatingBuyer = delivery.getBuyerOrder().getBuyer().getRatingBuyer() != null ? delivery.getBuyerOrder().getBuyer().getRatingBuyer() : 0;

        if(status == DeliveryStatus.DELIVERED){
            delivery.getBuyerOrder().setBuyerOrderStatus(BuyerOrderStatus.DELIVERED);
            delivery.getBuyerOrder().getBuyer().setRatingBuyer(currentRatingBuyer + 2);

            Optional<User> sellerOpt = delivery.getBuyerOrder().getPartOfferToBuySet().stream()
                    .map(p -> p.getSellerOffer().getSeller())
                    .findFirst();
            if(sellerOpt.isPresent()){
              User seller =   sellerOpt.get();
                int currentRatingSeller = seller.getRatingSeller() != null ? seller.getRatingSeller() : 0;
               seller.setRatingSeller(currentRatingSeller + 1);
            }
        }
        else if(status == DeliveryStatus.CANCELLED){
            delivery.getBuyerOrder().getBuyer().setRatingBuyer(currentRatingBuyer - 1);
            //TODO освободить дрон от доставки через RestClient
        }

        deliveryRepository.save(delivery);

        return deliveryMapper.toDeliveryResponse(delivery);
    }


    /**
     * метод отмены доставки.
     *
     * @param id идентификатор доставки покупателя
     * @return получение отмененной доставки.
     */
    @Override
    public DeliveryResponse cancelMyDelivery(Long id) {

        Delivery delivery = deliveryRepository.findDeliveryByIdWithDrone(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);
        Drone drone = delivery.getDrone();
        droneService.cancelDrone(drone.getId());

        drone.setDroneStatus(DroneStatus.BACK_TO_BASE);//??? установка статуса происходит во внешнем сервисе

        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public void deleteDelivery(Long id) {
        deliveryRepository.deleteById(id);
    }
}
