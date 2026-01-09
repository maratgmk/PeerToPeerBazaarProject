package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.BasketService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Basket Controller", description = "Provides endpoints for managing user basket.")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(
        path = "basket",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BasketController {
    private final BasketService basketService;

    @Operation(summary = "Retrieve basket.", description = "Retrieves a specific user's basket details using his Id.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#id,authentication)")
    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public BasketResponse get(@NotNull @Positive @PathVariable("id") Long id){
        return basketService.get(id);
    }

    @Operation(summary = "Add to basket number of seller offer parts.",
            description = "Allows a user to add a specified quantity of a seller's offer items to his basket.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#id,authentication)")
    @PostMapping(path = "/{id}/add", consumes = MediaType.ALL_VALUE)
    public BasketResponse addPartOfferToBuy(@NotNull @Positive @PathVariable Long id,
                                            @NotNull @Positive @RequestParam Long sellerOfferId,
                                            @NotNull @Positive @RequestParam Integer unitCount){
        return basketService.addPartOfferToBuy(id,sellerOfferId,unitCount);
    }

    @Operation(summary = "Remove from basket part seller offer.", description = "Removes a specific part seller offer from the user's basket.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#id,authentication)")
    @PostMapping(value = "/{id}/remove",consumes = MediaType.ALL_VALUE)
    public BasketResponse removePartOfferToBuy(@NotNull @Positive @PathVariable Long id,
                                               @NotNull @Positive @RequestParam Long partOfferToBuyId){
        return basketService.removePartOfferToBuy(id,partOfferToBuyId);
    }
}

