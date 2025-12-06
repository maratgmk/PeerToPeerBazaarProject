package org.gafiev.peertopeerbazaar.controller;

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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "sellerOffer",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerOfferController {
    private final SellerOfferService sellerOfferService;

    @PreAuthorize("@authz.isSelf(#sellerId, authentication)")
    @PostMapping
    public SellerOfferResponse createSellerOffer(@NotNull @Positive @RequestParam Long sellerId, @Valid @RequestBody SellerOfferCreateRequest sellerOfferCreate) {
        return sellerOfferService.createSellerOffer(sellerId, sellerOfferCreate);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public SellerOfferResponse getSellerOfferById(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long userId) {
        return sellerOfferService.getSellerOfferById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @GetMapping(path = "/{id}/part/user/{sellerId}", consumes = MediaType.ALL_VALUE)
    SellerOfferResponse getSellerOfferByIdWithPartOfferToBuy(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long sellerId, @RequestParam(
            value = "part",
            required = false,
            defaultValue = "false") Boolean isPart) {
        return isPart ? sellerOfferService.getSellerOfferByIdWithPartOfferToBuy(id) : sellerOfferService.getSellerOfferById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @GetMapping(path = "/allMy", consumes = MediaType.ALL_VALUE)
    public Set<SellerOfferResponse> getAllMySellerOffers(@NotNull @Positive @RequestParam Long sellerId) {
        return sellerOfferService.getAllMySellerOffers(sellerId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Set<SellerOfferResponse> getAllSellerOffers(@Valid @RequestBody SellerOfferFilterRequest filterRequest) {
        return sellerOfferService.getAllSellerOffers(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @PutMapping(path = "{id}/user/{sellerId}")
    public SellerOfferResponse updateMySellerOffer(@NotNull @Positive @PathVariable Long id,
                                                   @NotNull @Positive @PathVariable Long sellerId,
                                                   @Valid @RequestBody SellerOfferCreateRequest sellerOfferNew) {
        return sellerOfferService.updateMySellerOffer(sellerId, id, sellerOfferNew);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#sellerId, authentication)")
    @DeleteMapping(path = "{id}/user/{sellerId}",consumes = MediaType.ALL_VALUE)
    public void deleteSellerOffer(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long sellerId) {
        sellerOfferService.deleteSellerOffer(id);
    }
}