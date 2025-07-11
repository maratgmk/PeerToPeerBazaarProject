package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.AddressResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.AddressService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(
        path = "address",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public AddressResponse createAddress(@Valid @RequestBody AddressCreateRequest candidate) {
        return addressService.createAddress(candidate);
    }

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public AddressResponse getAddressById(@NotNull @Positive @PathVariable Long id) {
        return addressService.getAddressById(id);
    }

    @PostMapping("/filter")
    public Set<AddressResponse> getAllAddresses(@Valid @RequestBody AddressFilterRequest filterRequest) {
        return addressService.getAllAddresses(filterRequest);
    }

    @PostMapping("/user")
    public Set<AddressResponse> getAllMyAddresses(@NotNull @Positive @RequestParam Long userId) {
        return addressService.getAllMyAddresses(userId);
    }

    @PutMapping("/{id}")
    public AddressResponse updateMyAddress(
            @NotNull @Positive @PathVariable Long id,
            @NotNull @Positive @RequestParam Long userId,
            @Valid @RequestBody AddressCreateRequest addressNew) {
        return addressService.updateMyAddress(id, userId, addressNew);
    }

    @DeleteMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public void deleteAddress(@NotNull @Positive @PathVariable Long id) {
        addressService.deleteAddress(id);
    }

}
