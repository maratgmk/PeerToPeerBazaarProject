package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.BasketService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public BasketResponse get(@NotNull @Positive @PathVariable("id") Long id){
        return basketService.get(id);
    }

    @PostMapping(path = "/{id}/add", consumes = MediaType.ALL_VALUE)
    public BasketResponse addPartOfferToBuy(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @RequestParam Long sellerOfferId, @NotNull @Positive @RequestParam Integer unitCount){
        return basketService.addPartOfferToBuy(id,sellerOfferId,unitCount);
    }

    @PostMapping(value = "/{id}/remove",consumes = MediaType.ALL_VALUE)
    public BasketResponse removePartOfferToBuy(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @RequestParam Long partOfferToBuyId){
        return basketService.removePartOfferToBuy(id,partOfferToBuyId);
    }

}

