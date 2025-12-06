package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryUpdateTime;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DeliveryService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(
        path = "delivery",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public DeliveryResponse getDeliveryById(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        return deliveryService.getDeliveryById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/order/{buyerOrderId}/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(@NotNull @Positive @PathVariable Long buyerOrderId, @NotNull @Positive @PathVariable Long buyerId) {
        return deliveryService.getMyDeliveriesByBuyerOrderId(buyerOrderId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public Set<DeliveryResponse> getAllDeliveriesByFilter(@Valid @RequestBody DeliveryFilterRequest filterRequest) {
        return deliveryService.getAllDeliveriesByFilter(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @PostMapping(path = "/user/{buyerId}")
    public DeliveryResponse create(@NotNull @Positive @PathVariable Long buyerId, @Valid @NotNull @RequestBody DeliveryCreateRequest request) {
        return deliveryService.create(request);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/time/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public List<TimeSlotResponse> getTimeSlots(@Positive @NotNull @PathVariable Long id, @Positive @NotNull @PathVariable Long buyerId) {
        List<TimeSlotResponse> responseList = deliveryService.takeTimeSlots(id);
        log.info("Полученные времена от сервиса дронов {}", responseList);
        return responseList;
    }

    @PreAuthorize("@authz.isSelf(#buyerId, authentication)")
    @PutMapping("/{id}/user/{buyerId}")
    public DeliveryResponse assignDroneForDelivery(@NotNull @Positive @PathVariable Long id,
                                                   @NotNull @Positive @PathVariable Long buyerId,
                                                   @Valid @RequestBody DeliveryUpdateTime updateRequest) {
        log.info("Updating delivery time slot for delivery ID: {}", id);
        log.info("Received update request: {}", updateRequest);
        try {
            DeliveryResponse response = deliveryService.assignDroneForDelivery(id, updateRequest);
            log.info("Successfully drone assigned for delivery ID: {}", id);
            return response;
        } catch (Exception e) {
            log.error("Error updating delivery time slot for delivery ID: {}", id, e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(value = "/{id}/cancel/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public DeliveryResponse cancelMyDelivery(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long buyerId) {
        return deliveryService.updateStatus(id, DeliveryStatus.CANCELLED_BY_BUYER);
    }
    // DeliveryStatus.CANCELLED_BY_BUYER эта константа передаётся в сервис, только один вариант отмены

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{id}", consumes = MediaType.ALL_VALUE)
    public void deleteDelivery(@NotNull @Positive @PathVariable Long id) {
        deliveryService.deleteDelivery(id);
    }
}
