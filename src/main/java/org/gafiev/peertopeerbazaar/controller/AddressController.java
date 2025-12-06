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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public AddressResponse createAddress(@Valid @RequestBody AddressCreateRequest addressRequest) {
        return addressService.createAddress(addressRequest);
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
    @GetMapping("/user")
    public Set<AddressResponse> getAllMyAddresses(@NotNull @Positive @RequestParam Long userId) {
        return addressService.getAllMyAddresses(userId);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @PostMapping(value = "/create/user/{userId}",consumes = MediaType.ALL_VALUE)
    public AddressResponse createAddress(@NotNull @Positive @PathVariable Long userId,
            @Valid @RequestBody AddressCreateRequest createRequest) {
        return addressService.createAddress(createRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @PutMapping("/update/{id}/user/{userId}")
    public AddressResponse updateMyAddress(
            @NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long userId,
            @Valid @RequestBody AddressCreateRequest addressNew) {
        return addressService.updateMyAddress(id, addressNew);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @DeleteMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public void deleteAddress(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long userId) {
        addressService.deleteAddress(id);
    }

}
