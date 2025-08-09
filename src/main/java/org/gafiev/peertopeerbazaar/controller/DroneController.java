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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public DroneResponse getDroneById(@Positive @NotNull @PathVariable Long id) {
        return droneService.getDroneById(id);
    }

    @PostMapping("/filter")
    public Set<DroneResponse> getAllDrones(@Valid @RequestBody DroneFilterRequest filterRequest) {
        return droneService.getAllDrones(filterRequest);
    }

    @PutMapping("/{id}")
    public DroneResponse update(@Positive @NotNull @PathVariable Long id, @Valid @RequestBody DroneCreateRequest droneRequest) {
        return droneService.update(id, droneRequest);
    }

    @GetMapping(path = "/delivery/{id}", consumes = MediaType.ALL_VALUE)
    public List<TimeSlotResponse> getTimeSlots(@Positive @NotNull @PathVariable Long id){
        return droneService.getTimeSlots(id);
    }

    @GetMapping(path = "/cancel/{id}",consumes = MediaType.ALL_VALUE)
    public DroneResponse cancelDrone(@Positive @NotNull @PathVariable Long id){
        return droneService.cancelDrone(id);
    }
}

