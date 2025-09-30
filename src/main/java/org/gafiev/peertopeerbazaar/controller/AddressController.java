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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AddressResponse createAddress(@Valid @RequestBody AddressCreateRequest candidate) {
        return addressService.createAddress(candidate);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public AddressResponse getAddressById(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long userId ) {
        return addressService.getAddressById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public Set<AddressResponse> getAllAddresses(@Valid @RequestBody AddressFilterRequest filterRequest) {
        return addressService.getAllAddresses(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @PostMapping("/user")
    public Set<AddressResponse> getAllMyAddresses(@NotNull @Positive @RequestParam Long userId) {
        return addressService.getAllMyAddresses(userId);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @PutMapping("/{id}user/{userId}")
    public AddressResponse updateMyAddress(
            @NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long userId,
            @Valid @RequestBody AddressCreateRequest addressNew) {
        return addressService.updateMyAddress(id, addressNew);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @DeleteMapping(path = "/{id}user/{userId}", consumes = MediaType.ALL_VALUE)
    public void deleteAddress(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long userId) {
        addressService.deleteAddress(id);
    }

}
