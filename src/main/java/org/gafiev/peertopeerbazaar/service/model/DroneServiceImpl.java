package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.mapper.DroneMapper;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.DroneRepository;
import org.gafiev.peertopeerbazaar.repository.specification.DroneSpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DroneService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class DroneServiceImpl implements DroneService {
    private final DroneRepository droneRepository;
    private final ExternalDroneService externalDroneService;
    private final DroneMapper droneMapper;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DroneResponse getDroneById(Long id) {
        Drone drone = droneRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));
        return droneMapper.toDroneResponse(drone);
    }

    @Override
    public DroneResponse getDroneByIdWithBuyerOrder(Long id) {
        Drone drone = droneRepository.findByIdWithDeliveriesAndBuyerOrder(id).orElseThrow(() -> new EntityNotFoundException(
                Drone.class, Map.of("id", String.valueOf(id))));
        return droneMapper.toDroneResponse(drone);
    }

    @Override
    public Set<DroneResponse> getAllDrones(DroneFilterRequest filterRequest) {
        List<Drone> droneList = droneRepository.findAll(DroneSpecification.filterByParams(filterRequest));
        return droneMapper.toDroneResponseSet(new HashSet<>(droneList));
    }

    @Override
    public DroneResponse update(Long id, DroneUpdateRequest droneRequest) {
        Drone drone = droneRepository.findByIdWithDeliveriesAndBuyerOrder(id)
                .orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));

        Set<Delivery> deliveryCurrentSet = new HashSet<>(drone.getDeliverySet());

        Set<Long> deliveryIdsToAdd = droneRequest.deliveryIdsToAdd();

        if (deliveryIdsToAdd != null && !deliveryIdsToAdd.isEmpty()) {
            Set<Delivery> deliveryToAddSet = droneRequest.deliveryIdsToAdd().stream()
                    .filter(addId -> deliveryCurrentSet.stream().noneMatch(delivery -> delivery.getId().equals(addId)))
                    .map(addId -> deliveryRepository.findById(addId)
                            .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(addId)))))
                    .collect(Collectors.toSet());

            deliveryCurrentSet.addAll(deliveryToAddSet);
        }
        drone.setDeliverySet(deliveryCurrentSet);

        Set<Long> deliveryIdsToRemove = droneRequest.deliveryIdsToRemove();

        if (deliveryIdsToRemove != null && !deliveryIdsToRemove.isEmpty()) {
            Set<Long> deliveryCurrentIdsSet = deliveryCurrentSet.stream()
                    .map(Delivery::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = deliveryIdsToRemove.stream()
                    .filter(removeId -> !deliveryCurrentIdsSet.contains(removeId))
                    .toList();

            if (!missingIds.isEmpty()) {
                throw new IllegalArgumentException(
                        "The following delivery IDs are not assigned to the drone and cannot be removed: " + missingIds
                );
            }
            deliveryCurrentSet.removeIf(delivery -> deliveryIdsToRemove.contains(delivery.getId()));
        }
        drone.setDeliverySet(deliveryCurrentSet);
        drone = droneRepository.save(drone);
        return droneMapper.toDroneResponse(drone);
    }

    @Override
    public List<TimeSlotResponse> getTimeSlots(Long deliveryId) {
        Delivery delivery = deliveryRepository.findDeliveryByIdWithBuyerOrderWithAddresses(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(deliveryId))));

        Set<TimeSlotResponse> timeSlotResponses = externalDroneService.requestDroneSchedule(deliveryMapper.toDeliveryDroneRequest(delivery));

        return timeSlotResponses.stream().sorted(Comparator.comparing(TimeSlotResponse::start)).toList();
    }

    @Override
    public DroneResponse cancelDrone(Long id, Long deliveryId) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(deliveryId))));
        ExternalDroneResponse externalDroneResponse = externalDroneService.cancelDrone(drone.getDroneServiceId(), delivery.getId());

        return droneMapper.toDroneResponse(externalDroneResponse);
    }
}
