package org.gafiev.peertopeerbazaar.service.integration;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ExternalDroneServiceImpl implements ExternalDroneService {
    /**
     * статусы доставок, при которых нужно отслеживать статусы дронов.
     */
    public static final Set<DeliveryStatus> DELIVERY_STATUSES = Set.of(
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
//                .uri("/{droneServiceId}/status")
//                .contentType(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .body(ExternalDroneResponse.class);
          .uri(uriBuilder -> uriBuilder
                .path("/{droneServiceId}/status")
                .queryParam("status", status)
                .build(droneServiceId))
                .retrieve()// Этот метод бросает HttpServerErrorException при 5xx от внешнего сервиса
                .body(ExternalDroneResponse.class);
    }

    //TODO этот метод отсутствует во внешнем сервисе
    @Override
    public ExternalDroneResponse cancelDrone(Long droneServiceId, Long deliveryId) {
        try {
            return droneOperatorClient.get()
//                    .uri("/cancel/%d?deliveryId=%d".formatted(droneServiceId, deliveryId))
//                    .retrieve()// Этот метод бросает HttpServerErrorException при 5xx от внешнего сервиса
//                    .body(ExternalDroneResponse.class);
                    .uri(uriBuilder -> uriBuilder
                            .path("/cancel/{droneServiceId}")
                            .queryParam("deliveryId", deliveryId)
                            .build(droneServiceId))
                    .retrieve()// Этот метод бросает HttpServerErrorException при 5xx от внешнего сервиса
                    .body(ExternalDroneResponse.class);
        } catch (Exception e){
            log.error("Can't cancel drone droneServiceId = " + droneServiceId,e);
            throw new DroneException("Can't cancel drone droneServiceId = " + droneServiceId, e);
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
        log.info("checkAllDroneStatus started");
        Set<Delivery> deliveries = deliveryRepository.findAllByStatusesWithDroneAndBuyerOrderAndBuyer(DELIVERY_STATUSES);
        Map<Long, Drone> drones = deliveries.stream()
                .map(Delivery::getDrone)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Drone::getDroneServiceId, drone -> drone));

        Set<ExternalDroneResponse> externalDrones = getAllDronesExternal(ExternalDroneFilterRequest.builder()
                .droneServiceIds(drones.keySet())
                .build());

        log.info("Данные получены от getAllDronesExternal: externalDrones = {}", externalDrones);

        externalDrones.forEach(externalDrone -> {
            Drone drone = drones.get(externalDrone.droneServiceId());
            if (drone == null) return;
            drone.setDroneStatus(externalDrone.droneStatus());
            if (drone.getDroneStatus() == DroneStatus.OFFLOADED) {
                Delivery delivery = deliveries.stream()
                        .filter(d -> d.getDrone().equals(drone))
                        .findFirst().orElseThrow();
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);

                delivery.getBuyerOrder().setBuyerOrderStatus(BuyerOrderStatus.DELIVERED);
                int currentRatingBuyer = delivery.getBuyerOrder().getBuyer().getRatingBuyer() != null ? delivery.getBuyerOrder().getBuyer().getRatingBuyer() : 0;
                delivery.getBuyerOrder().getBuyer().setRatingBuyer(currentRatingBuyer + 2);

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
        log.info("Данные после обновления дронов и сохранении в репозитории : {}", drones.values());
    }
}
