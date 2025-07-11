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
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DeliveryService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public DeliveryResponse getDeliveryById(@NotNull @Positive @PathVariable Long id) {
        return deliveryService.getDeliveryById(id);
    }

    @GetMapping(path = "/order", consumes = MediaType.ALL_VALUE)
    public Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(@NotNull @Positive @RequestParam Long buyerOrderId) {
        return deliveryService.getMyDeliveriesByBuyerOrderId(buyerOrderId);
    }

    @PostMapping("/all")
    public Set<DeliveryResponse> getAllDeliveries(
            @Valid @RequestParam DeliveryStatus deliveryStatus,
            @Valid @RequestBody Address toAddress,
            @Valid @RequestBody Address fromAddress,
            @Valid @RequestBody TimeSlot timeSlot) {
        return deliveryService.getAllDeliveries(deliveryStatus, toAddress, fromAddress, timeSlot);
    }

    @PostMapping("/filter")
    public Set<DeliveryResponse> getAllDeliveriesByFilter(@Valid @RequestBody DeliveryFilterRequest filterRequest) {
        return deliveryService.getAllDeliveriesByFilter(filterRequest);
    }

    @PostMapping
    public DeliveryResponse create(@Valid @NotNull @RequestBody DeliveryCreateRequest request) {
        return deliveryService.create(request);
    }

    @GetMapping (path = "/{id}/time", consumes = MediaType.ALL_VALUE)
    public List<TimeSlotResponse> getTimeSlots(@Positive @NotNull @PathVariable Long id) {
        List<TimeSlotResponse> responseList = deliveryService.takeTimeSlots(id);
        log.info("Полученные времена от сервиса дронов {}", responseList);
        return responseList;
    }


    @GetMapping(path = "/{failDeliveryId}/new", consumes = MediaType.ALL_VALUE)
    public DeliveryResponse createDeliveryDependsOnFail(@NotNull @Positive @PathVariable Long failDeliveryId) {
        return deliveryService.createDeliveryDependsOnFail(failDeliveryId);
    }

    @PutMapping("/{id}")
    public DeliveryResponse assignDroneForDelivery(@NotNull @Positive @PathVariable Long id, @Valid @RequestBody DeliveryUpdateTime updateRequest) {
        log.info("Updating delivery time slot for delivery ID: {}", id);
        log.debug("Received update request: {}", updateRequest);
        try {
            DeliveryResponse response = deliveryService.assignDroneForDelivery(id, updateRequest);
            log.info("Successfully drone assigned for delivery ID: {}", id);
            return response;
        } catch (Exception e) {
            log.error("Error updating delivery time slot for delivery ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}/cancel", consumes = MediaType.ALL_VALUE)
    public DeliveryResponse cancelMyDelivery(
            @NotNull @Positive @PathVariable Long id) {
        return deliveryService.cancelMyDelivery(id);
    }

    @PostMapping("/{id}/confirm")
    public DeliveryResponse confirmDelivery(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @RequestParam Long userId){
        return deliveryService.updateStatus(id,DeliveryStatus.DELIVERED);
    }

    @DeleteMapping("{id}")
    public void deleteDelivery(@NotNull @Positive @PathVariable Long id) {
        deliveryService.deleteDelivery(id);
    }
}
