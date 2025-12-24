package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DroneService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Tag(name = "Drones", description = "Operations for managing drone deliveries and tracking.")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(
        path = "drone",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DroneController {
    private final DroneService droneService;

    @Operation(summary = "Get drone by ID", description = "Retrieves details of a specific drone using its unique identifier.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public DroneResponse getDroneById(@Positive @NotNull @PathVariable Long id, @Positive @NotNull @PathVariable Long userId) {
        return droneService.getDroneById(id);
    }

    @Operation(summary = "Filter drones", description = "Allows users with the ADMIN role to retrieve drones matching specific filter criteria.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public Set<DroneResponse> getAllDrones(
            @Parameter(description = "Filter criteria for searching drones.", required = true)
            @Valid @RequestBody DroneFilterRequest filterRequest) {
        return droneService.getAllDrones(filterRequest);
    }

    @Operation(summary = "Update drone", description = "Updates an existing drone record using the provided request data.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @PutMapping("/{id}/user/{userId}")
    public DroneResponse update(@Positive @NotNull @PathVariable Long id, @Positive @NotNull @PathVariable Long userId,
                                @Parameter(description = "Data for updating an existing drone.", required = true)
                                @Valid @RequestBody DroneUpdateRequest droneRequest) {
        return droneService.update(id, droneRequest);
    }

    @Operation(summary = "Get available time slots", description = "Retrieves time slots for a specific delivery from the external service.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/delivery/{deliveryId}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public List<TimeSlotResponse> getTimeSlots(@Positive @NotNull @PathVariable Long deliveryId, @Positive @NotNull @PathVariable Long userId) {
        return droneService.getTimeSlots(deliveryId);
    }

    @Operation(summary = "Cancel drone assignment",
            description = "Cancels a drone assignment for a specific delivery via external service.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/cancel/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public DroneResponse cancelDrone(@Positive @NotNull @PathVariable Long id,
                                     @Positive @NotNull @PathVariable Long userId,
                                     @Parameter(description = "Associated delivery ID", required = true)
                                     @Positive @NotNull @RequestParam(required = true) Long deliveryId) {
        return droneService.cancelDrone(id, deliveryId);
    }
}

