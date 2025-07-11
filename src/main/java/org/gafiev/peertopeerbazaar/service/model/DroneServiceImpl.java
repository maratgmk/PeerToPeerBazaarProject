package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.mapper.DroneMapper;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.DroneRepository;
import org.gafiev.peertopeerbazaar.repository.specification.DroneSpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DroneService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class DroneServiceImpl implements DroneService {
    private final DroneRepository droneRepository;
    private final ExternalDroneService externalDroneService;
    private final DroneMapper droneMapper;
    private final DeliveryRepository deliveryRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DroneResponse getDroneById(Long id) {
        Drone drone = droneRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));
        return droneMapper.toDroneResponse(drone);
    }

    /**
     * в этом методе getAllDrones все данные из репозитория получаются обновленными согласно метода по расписанию?
     */
    @Override
    public Set<DroneResponse> getAllDrones(DroneFilterRequest filterRequest) {
        List<Drone> droneList = droneRepository.findAll(DroneSpecification.filterByParams(filterRequest));
        return droneMapper.toDroneResponseSet(new HashSet<>(droneList));
    }

    /**
     * Это метод по загрузке и разгрузке дрона. Метод вызывается дважды, сначала при загрузке, затем, когда прилетит к покупателю, то будет разгрузка.
     * метод по обновлению дрона, изначальные drone.getDeliverySet() затираются новыми доставками из droneRequest.deliveryIdsToAdd()
     * и в добавок из нового множества полученных стираются ещё раз те, которые совпадают с droneRequest.deliveryIdsToRemove(). Зачем так?
     * @param id идентификатор дрона, который надо обновить
     * @param droneRequest DTO информация, необходимая для обновления дрона
     * @return DTO изменённого дрона
     */
    @Override
    public DroneResponse update(Long id, DroneCreateRequest droneRequest) {
        Drone drone = droneRepository.findByIdWithDeliveriesAndBuyerOrders(id)
                .orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));
        Set<Long> droneDeliveryIds = drone.getDeliverySet().stream().map(Delivery::getId).collect(Collectors.toSet());

        if (!droneDeliveryIds.equals(droneRequest.deliveryIdsToAdd())) {
            Set<Delivery> deliveryNewSet = droneRequest.deliveryIdsToAdd().stream()
                    .map(deliveryId -> deliveryRepository.findById(deliveryId)
                            .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(deliveryId)))))
                    .collect(Collectors.toSet());
            drone.setDeliverySet(deliveryNewSet);//стерли первоначальный Set = drone.getDeliverySet(), и заменили на droneRequest.deliveryIdsToAdd().
            // Это что за логика? Логика сейчас через месяц стала понятна.)  Но главный вопрос, откуда первоначально взялся первоначальный Set = drone.getDeliverySet()?
            // Где произошла загрузка?
        }

        if (!droneDeliveryIds.equals(droneRequest.deliveryIdsToRemove())) {
            Set<Delivery> deliveryNewSet = droneRequest.deliveryIdsToRemove().stream()
                    .map(deliveryId -> deliveryRepository.findById(deliveryId)
                            .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(deliveryId)))))
                    .collect(Collectors.toSet());
            drone.setDeliverySet(deliveryNewSet);
        }
        drone = droneRepository.save(drone);

        return droneMapper.toDroneResponse(drone);
    }

    /**
     * метод по обновлению дрона, к изначальному множеству доставок = drone.getDeliverySet() добавляются новые доставки из droneRequest.deliveryIdsToAdd()
     * и удаляются доставки из droneRequest.deliveryIdsToRemove().
     * @param id идентификатор дрона, который надо обновить
     * @param droneRequest DTO информация, необходимая для обновления дрона
     * @return DTO изменённого дрона
     */
    @Override
    public DroneResponse update2(Long id, DroneCreateRequest droneRequest) {
        Drone drone = droneRepository.findByIdWithDeliveriesAndBuyerOrders(id)
                .orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));

            Set<Delivery> deliveryCurrentSet = new HashSet<>(drone.getDeliverySet());

            Set<Long> deliveryIdsToAdd = droneRequest.deliveryIdsToAdd();
            if(deliveryIdsToAdd != null && !deliveryIdsToAdd.isEmpty()){
                Set<Delivery> deliveryToAddSet = droneRequest.deliveryIdsToAdd().stream()
                        .filter(addId -> deliveryCurrentSet.stream().noneMatch(delivery -> delivery.getId().equals(addId)))
                        .map(addId -> deliveryRepository.findById(addId)
                                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(addId)))))
                        .collect(Collectors.toSet());

                deliveryCurrentSet.addAll(deliveryToAddSet);
            }
            drone.setDeliverySet(deliveryCurrentSet);

        Set<Long> deliveryIdsToRemove = droneRequest.deliveryIdsToRemove();
        if(deliveryIdsToRemove != null && !deliveryIdsToRemove.isEmpty()){
            Set<Long> deliveryCurrentIdsSet = deliveryCurrentSet.stream().map(Delivery::getId).collect(Collectors.toSet());

            for (Long deliveryId : deliveryIdsToRemove){
                if(!deliveryCurrentIdsSet.contains(deliveryId)){
                    throw new EntityNotFoundException(Delivery.class,Map.of("deliveryId", String.valueOf(deliveryId)));
                }
            }
            deliveryCurrentSet.removeIf(delivery -> deliveryIdsToRemove.contains(delivery.getId()));
        }
        drone.setDeliverySet(deliveryCurrentSet);
        drone = droneRepository.save(drone);
        return droneMapper.toDroneResponse(drone);
    }

    @Override
    public List<TimeSlotResponse> getTimeSlots(Long id) {
     Delivery delivery = deliveryRepository.findDeliveryByIdWithBuyerOrderWithAddresses(id)
             .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));

        Set<TimeSlotResponse> timeSlotResponses = externalDroneService.requestDroneSchedule(deliveryMapper.toDeliveryDroneRequest(delivery));

        return timeSlotResponses.stream().sorted(Comparator.comparing(TimeSlotResponse::start)).toList();
    }

    @Override
    public DroneResponse observingFlightOfDrone(Long id) {
        Drone drone = droneRepository.findByIdWithDeliveriesAndBuyerOrders(id)
                .orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));

        if(drone.getDroneStatus() == DroneStatus.OFFLOADED){
            drone.getDeliverySet().forEach(d -> {
                d.setDeliveryStatus(DeliveryStatus.DELIVERED);
                d.getBuyerOrder().setBuyerOrderStatus(BuyerOrderStatus.DELIVERED);
            });
            deliveryRepository.saveAll(drone.getDeliverySet());
        }

        return droneMapper.toDroneResponse(drone);
    }

    @Override
    public DroneResponse cancelDrone(Long id) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));
        ExternalDroneResponse externalDroneResponse = externalDroneService.cancelDrone(drone.getDroneServiceId());
        return droneMapper.toDroneResponse(externalDroneResponse);
    }
}
