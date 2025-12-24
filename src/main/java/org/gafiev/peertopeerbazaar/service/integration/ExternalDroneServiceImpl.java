package org.gafiev.peertopeerbazaar.service.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalDroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.DroneException;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.DroneRepository;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalDroneServiceImpl implements ExternalDroneService {
    /**
     * Delivery statuses that require drone status tracking.
     * Only deliveries in these states will be synchronized with the external drone service.
     */
    public static final Set<DeliveryStatus> MONITORED_DELIVERY_STATUSES = Set.of(
            DeliveryStatus.DRONE_ASSIGNED,
            DeliveryStatus.DELAYED,
            DeliveryStatus.ON_THE_WAY);
    private final RestClient droneOperatorClient;
    private final DeliveryRepository deliveryRepository;
    private final DroneRepository droneRepository;


    @Override
    public ExternalDroneResponse getDroneById(Long droneServiceId) {
        return droneOperatorClient.get()
                .uri("/" + droneServiceId)
                .retrieve()
                .body(ExternalDroneResponse.class);
    }

    @Override
    @Transactional
    public Set<ExternalDroneResponse> getAllDronesExternal(ExternalDroneFilterRequest filterRequest) {
        return droneOperatorClient.post()
                .uri("/all")
                .body(filterRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public ExternalDroneResponse requestDrone(DeliveryDroneRequest deliveryDroneRequest) {
        log.info("Making HTTP call to drone service: {}", deliveryDroneRequest);
        return droneOperatorClient.post()
                .uri("/assign")
                .body(deliveryDroneRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ExternalDroneResponse.class);
    }

    @Override
    public Set<TimeSlotResponse> requestDroneSchedule(DeliveryDroneRequest deliveryDroneRequest) {
        log.debug("Sending DeliveryDroneRequest to drone operator: {}", deliveryDroneRequest);

        return droneOperatorClient.post()
                .uri("/timeSlot")
                .body(deliveryDroneRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<Set<TimeSlotResponse>>() {
                });
    }

    @Override
    public ExternalDroneResponse changeStatus(Long droneServiceId, DroneStatus status) {
        return droneOperatorClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{droneServiceId}/status")
                        .queryParam("status", status)
                        .build(droneServiceId))
                .retrieve()
                .body(ExternalDroneResponse.class);
    }

    @Override
    public ExternalDroneResponse cancelDrone(Long droneServiceId, Long deliveryId) {
        try {
            return droneOperatorClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cancel/{droneServiceId}")
                            .queryParam("deliveryId", deliveryId)
                            .build(droneServiceId))
                    .retrieve()
                    .body(ExternalDroneResponse.class);
        } catch (Exception e) {
            log.error("Failed to cancel drone. droneServiceId: {}, deliveryId: {}", droneServiceId, deliveryId, e);
            throw new DroneException("External service failed to cancel drone with ID: " + droneServiceId, e);
        }
    }

    @Override
    public String getCode(AddressCreateRequest addressCreateRequest) {
        return droneOperatorClient.post()
                .uri("/code")
                .body(addressCreateRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }

    @Transactional
    @Scheduled(cron = "${cron.expression}")
    public void checkAllDroneStatus() {
        // Fetch active deliveries with all necessary relations to avoid N+1
        Set<Delivery> deliveries = deliveryRepository.findAllByStatusesWithDroneAndBuyerOrderAndBuyer(MONITORED_DELIVERY_STATUSES);
        Map<Long, Drone> drones = deliveries.stream()
                .map(Delivery::getDrone)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Drone::getDroneServiceId, drone -> drone));
        log.info("Starting drone status synchronization for {} active deliveries", deliveries.size());

        // Call external service to get actual drone statuses
        Set<ExternalDroneResponse> externalDrones = getAllDronesExternal(ExternalDroneFilterRequest.builder()
                .droneServiceIds(drones.keySet())
                .build());

        log.info("Received data from external service: externalDrones = {}", externalDrones);

        externalDrones.forEach(externalDrone -> {
            Drone drone = drones.get(externalDrone.droneServiceId());
            if (drone == null) return;
            drone.setDroneStatus(externalDrone.droneStatus());

            // If the drone has finished unloading, complete the delivery process
            if (drone.getDroneStatus() == DroneStatus.OFFLOADED) {
                Delivery delivery = deliveries.stream()
                        .filter(d -> d.getDrone().equals(drone))
                        .findFirst().orElseThrow();
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);

                delivery.getBuyerOrder().setBuyerOrderStatus(BuyerOrderStatus.DELIVERED);
                // Update buyer rating
                int currentRatingBuyer = delivery.getBuyerOrder().getBuyer().getRatingBuyer() != null ? delivery.getBuyerOrder().getBuyer().getRatingBuyer() : 0;
                delivery.getBuyerOrder().getBuyer().setRatingBuyer(currentRatingBuyer + 2);

                // Since one BuyerOrder always belongs to a single seller, we take the first available one
                // Each order is strictly limited to one seller by design (see BuyerOrderService.create)
                Optional<User> sellerOpt = delivery.getBuyerOrder().getPartOfferToBuySet().stream()
                        .map(p -> p.getSellerOffer().getSeller())
                        .findFirst();
                if (sellerOpt.isPresent()) {
                    User seller = sellerOpt.get();
                    int currentRatingSeller = seller.getRatingSeller() != null ? seller.getRatingSeller() : 0;
                    seller.setRatingSeller(currentRatingSeller + 1);
                }
            }
        });
        droneRepository.saveAll(drones.values());
        log.info("Drone statuses and related entities successfully updated for {} drones", drones.size());
    }
}
