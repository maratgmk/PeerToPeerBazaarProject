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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "sellerOffer",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerOfferController {
    private final SellerOfferService sellerOfferService;

    @PostMapping
    public SellerOfferResponse createSellerOffer(@NotNull @Positive @RequestParam Long sellerId, @Valid @RequestBody SellerOfferCreateRequest sellerOfferCreate) {
        return sellerOfferService.createSellerOffer(sellerId, sellerOfferCreate);
    }

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public SellerOfferResponse getSellerOfferById(@NotNull @Positive @PathVariable Long id) {
        return sellerOfferService.getSellerOfferById(id);
    }

    @GetMapping(path = "/{id}/part", consumes = MediaType.ALL_VALUE)
    SellerOfferResponse getSellerOfferByIdWithPartOfferToBuy(@NotNull @Positive @PathVariable Long id, @RequestParam(
            value = "part",
            required = false,
            defaultValue = "false") Boolean isPart) {
        return isPart ? sellerOfferService.getSellerOfferByIdWithPartOfferToBuy(id) : sellerOfferService.getSellerOfferById(id);
    }
    @GetMapping(path = "/{id}/unitCount", consumes = MediaType.ALL_VALUE)
    Integer getActualUnitCount(@NotNull @Positive @PathVariable Long id){
        return sellerOfferService.getActualUnitCount(id);
    }

    @GetMapping(path = "/allMy", consumes = MediaType.ALL_VALUE)
    public Set<SellerOfferResponse> getAllMySellerOffers(@NotNull @Positive @RequestParam Long sellerId) {
        return sellerOfferService.getAllMySellerOffers(sellerId);
    }

    @PostMapping("/all")
    public Set<SellerOfferResponse> getAllSellerOffers(@Valid @RequestBody SellerOfferFilterRequest filterRequest) {
        return sellerOfferService.getAllSellerOffers(filterRequest);
    }

    @PutMapping(path = "{id}")
    public SellerOfferResponse updateMySellerOffer(@NotNull @Positive @RequestParam Long sellerId,
                                                   @NotNull @Positive @PathVariable Long id,
                                                   @Valid @RequestBody SellerOfferCreateRequest sellerOfferNew) {
        return sellerOfferService.updateMySellerOffer(sellerId, id, sellerOfferNew);
    }

    @DeleteMapping(path = "{id}",consumes = MediaType.ALL_VALUE)
    public void deleteSellerOffer(@NotNull @Positive @PathVariable Long id) {
        sellerOfferService.deleteSellerOffer(id);
    }
}