package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Deliveries", description = "Operations for managing deliveries and tracking history.")
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

    @Operation(summary = "Get delivery by ID", description = "Retrieves details of a specific delivery using its unique identifier.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public DeliveryResponse getDeliveryById(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        return deliveryService.getDeliveryById(id);
    }

    @Operation(summary = "Get buyer deliveries by order ID",
            description = "Retrieves all deliveries associated with a specific buyer order identifier.\n")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/order/{buyerOrderId}/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(@NotNull @Positive @PathVariable Long buyerOrderId, @NotNull @Positive @PathVariable Long buyerId) {
        return deliveryService.getMyDeliveriesByBuyerOrderId(buyerOrderId);
    }

    @Operation(summary = "Filter deliveries",
            description = "Allows users with the ADMIN role to retrieve deliveries matching specific filter criteria.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public Set<DeliveryResponse> getAllDeliveriesByFilter(
            @Parameter(description = "Filter criteria for searching deliveries.", required = true)
            @Valid @RequestBody DeliveryFilterRequest filterRequest) {
        return deliveryService.getAllDeliveriesByFilter(filterRequest);
    }

    @Operation(summary = "Create delivery", description = "Creates a new delivery record using the provided request data.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @PostMapping(path = "/user/{buyerId}")
    public DeliveryResponse create(@NotNull @Positive @PathVariable Long buyerId,
                                   @Parameter(description = "Data for creating a new delivery.", required = true)
                                   @Valid @NotNull @RequestBody DeliveryCreateRequest request) {
        return deliveryService.create(request);
    }

    @Operation(summary = "Get available time slots", description = "Retrieves available delivery time slots from the external drone service.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/time/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public List<TimeSlotResponse> getTimeSlots(@Positive @NotNull @PathVariable Long id, @Positive @NotNull @PathVariable Long buyerId) {
        List<TimeSlotResponse> responseList = deliveryService.takeTimeSlots(id);
        log.info("Available time slots from the external drone service{}", responseList);
        return responseList;
    }

    @Operation(summary = "Assign drone to delivery", description = "Assigns a drone for a specific time slot via the external drone service.")
    @PreAuthorize("@authz.isSelf(#buyerId, authentication)")
    @PutMapping("/{id}/user/{buyerId}")
    public DeliveryResponse assignDroneForDelivery(@NotNull @Positive @PathVariable Long id,
                                                   @NotNull @Positive @PathVariable Long buyerId,
                                                   @Parameter(description = "Details of the selected time slot.", required = true)
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

    @Operation(summary = "Cancel delivery", description = "Allows a user with ADMIN or BUYER role to cancel a delivery.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(value = "/{id}/cancel/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public DeliveryResponse cancelMyDelivery(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long buyerId) {
        return deliveryService.updateStatus(id, DeliveryStatus.CANCELLED_BY_BUYER);
    }

    @Operation(summary = "Delete delivery", description = "Permanently removes a delivery record. Access restricted to ADMIN users.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{id}", consumes = MediaType.ALL_VALUE)
    public void deleteDelivery(@NotNull @Positive @PathVariable Long id) {
        deliveryService.deleteDelivery(id);
    }
}
