package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Addresses", description = "Provides endpoints for managing user's addresses.")
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

    @Operation(summary = "Creates address.", description = "Helps user to create new address.")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AddressResponse createAddress(
            @Parameter(description = "Data for address creation.", required = true)
            @Valid @RequestBody AddressCreateRequest addressRequest) {
        return addressService.createAddress(addressRequest);
    }

    @Operation(summary = "Gets address by Id.", description = "Helps user to get address by its Id.")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public AddressResponse getAddressById(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        return addressService.getAddressById(id);
    }

    @Operation(summary = "Gets all addresses according to filter criteria.", description = "Helps user with role ADMIN to get all addresses by special filter.")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public Set<AddressResponse> getAllAddresses(
            @Parameter(description = "Data for the specific filter to get all interested addresses.", required = true)
            @Valid @RequestBody AddressFilterRequest filterRequest) {
        return addressService.getAllAddresses(filterRequest);
    }

    @Operation(summary = "Gets all user addresses.", description = "Helps user to get all his addresses by user Id.")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping("/user")
    public Set<AddressResponse> getAllMyAddresses(@NotNull @Positive @RequestParam Long userId) {
        return addressService.getAllMyAddresses(userId);
    }

    @Operation(summary = "Updates user address.", description = "Helps user to update his existing address by new parameters.")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @PutMapping("/update/{id}/user/{userId}")
    public AddressResponse updateMyAddress(
            @NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId,
            @Parameter(description = "Data for update address.", required = true)
            @Valid @RequestBody AddressCreateRequest addressNew) {
        return addressService.updateMyAddress(id, addressNew);
    }

    @Operation(summary = "Deletes address.", description = "Helps user with role ADMIN to delete address by Id.")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @DeleteMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public void deleteAddress(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        addressService.deleteAddress(id);
    }
}
