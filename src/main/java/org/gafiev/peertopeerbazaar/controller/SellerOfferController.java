package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.SellerOfferResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.SellerOfferService;
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

@Tag(name = "SellerOffers", description = "Provides endpoints for managing seller offer data.")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "sellerOffer",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerOfferController {
    private final SellerOfferService sellerOfferService;

    @Operation(summary = "Creates a seller offer.", description = "Allows a user to create a new seller offer using data from the DTO.")
    @PreAuthorize("@authz.isSelf(#sellerId, authentication)")
    @PostMapping
    public SellerOfferResponse createSellerOffer(@NotNull @Positive @RequestParam Long sellerId,
                                                 @Parameter(description = "Data for seller offer creation.", required = true)
                                                 @Valid @RequestBody SellerOfferCreateRequest sellerOfferCreate) {
        return sellerOfferService.createSellerOffer(sellerId, sellerOfferCreate);
    }

    @Operation(summary = "Retrieves a seller offer by ID.", description = "Allows a user to retrieve a specific seller offer using its ID.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public SellerOfferResponse getSellerOfferById(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        return sellerOfferService.getSellerOfferById(id);
    }

    @Operation(summary = "Retrieves a seller offer along with associated parts.",
            description = "Retrieves a seller offer, including all associated parts (eagerly fetched).")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @GetMapping(path = "/{id}/part/user/{sellerId}", consumes = MediaType.ALL_VALUE)
    SellerOfferResponse getSellerOfferByIdWithPartOfferToBuy(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long sellerId, @RequestParam(
            value = "part",
            required = false,
            defaultValue = "false") Boolean isPart) {
        return isPart ? sellerOfferService.getSellerOfferByIdWithPartOfferToBuy(id) : sellerOfferService.getSellerOfferById(id);
    }

    @Operation(summary = "Retrieves all seller offers for a specific seller.",
            description = "Retrieves all seller offers created by the user.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @GetMapping(path = "/allMy", consumes = MediaType.ALL_VALUE)
    public Set<SellerOfferResponse> getAllMySellerOffers(@NotNull @Positive @RequestParam Long sellerId) {
        return sellerOfferService.getAllMySellerOffers(sellerId);
    }

    @Operation(summary = "Retrieves seller offers using a filter.",
            description = "Helps a user with ADMIN role to retrieve seller offers based on the filter criteria.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Set<SellerOfferResponse> getAllSellerOffers(
            @Parameter(description = "Filter criteria data for the dynamic SQL query\"", required = true)
            @Valid @RequestBody SellerOfferFilterRequest filterRequest) {
        return sellerOfferService.getAllSellerOffers(filterRequest);
    }

    @Operation(summary = "Updates an existing seller offer.",
            description = "Allows a user to update the details of his own seller offer.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @PutMapping(path = "{id}/user/{sellerId}")
    public SellerOfferResponse updateMySellerOffer(@NotNull @Positive @PathVariable Long id,
                                                   @NotNull @Positive @PathVariable Long sellerId,
                                                   @Parameter(description = "New data for updating the seller offer.", required = true)
                                                   @Valid @RequestBody SellerOfferCreateRequest sellerOfferNew) {
        return sellerOfferService.updateMySellerOffer(sellerId, id, sellerOfferNew);
    }

    @Operation(summary = "Deletes a seller offer by ID.", description = "Allows a user to delete his seller offer using its ID.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @DeleteMapping(path = "{id}/user/{sellerId}", consumes = MediaType.ALL_VALUE)
    public void deleteSellerOffer(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long sellerId) {
        sellerOfferService.deleteSellerOffer(id);
    }
}