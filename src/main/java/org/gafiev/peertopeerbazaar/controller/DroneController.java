package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
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

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public DroneResponse getDroneById(@Positive @NotNull @PathVariable Long id, @Positive @NotNull @PathVariable Long userId ) {
        return droneService.getDroneById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")  // somebody else ?
    @PostMapping("/filter")
    public Set<DroneResponse> getAllDrones(@Valid @RequestBody DroneFilterRequest filterRequest) {
        return droneService.getAllDrones(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @PutMapping("/{id}/user/{userId}")
    public DroneResponse update(@Positive @NotNull @PathVariable Long id,@Positive @NotNull @PathVariable Long userId,
                                @Valid @RequestBody DroneCreateRequest droneRequest) {
        return droneService.update(id, droneRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/delivery/{deliveryId}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public List<TimeSlotResponse> getTimeSlots(@Positive @NotNull @PathVariable Long deliveryId,@Positive @NotNull @PathVariable Long userId){
        return droneService.getTimeSlots(deliveryId);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/cancel/{id}/user/{userId}",consumes = MediaType.ALL_VALUE)
    public DroneResponse cancelDrone(@Positive @NotNull @PathVariable Long id, @Positive @NotNull @PathVariable Long userId,
                                     @Positive @NotNull @RequestParam(required = true) Long deliveryId){
        return droneService.cancelDrone(id, deliveryId);
    }

}

