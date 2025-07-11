package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;
import org.gafiev.peertopeerbazaar.service.model.interfaces.BuyerOrderService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(
        path = "buyerOrder",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BuyerOrderController {
    private final BuyerOrderService buyerOrderService;

    @PostMapping
    public Set<BuyerOrderResponse> create(@NotNull @Positive @RequestParam Long buyerId, @Valid @RequestBody BuyerOrderCreateRequest candidate) {
        return buyerOrderService.create(buyerId, candidate);
    }

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public BuyerOrderResponse get(@NotNull @Positive @RequestParam Long buyerId,@NotNull @Positive @PathVariable Long id) {
        return buyerOrderService.get(buyerId, id);
    }

    @PostMapping(value = "/status",consumes = MediaType.ALL_VALUE)
    public Set<BuyerOrderResponse> getAll(@NotNull @Positive @RequestParam Long buyerId, @NotNull @RequestParam BuyerOrderStatus buyerOrderStatus) {
        return buyerOrderService.getAll(buyerId, buyerOrderStatus);
    }

    @PostMapping("/filter")
    public Set<BuyerOrderResponse> getAllBuyerOrders(@Valid @RequestBody BuyerOrderFilterRequest filterRequest) {
        return buyerOrderService.getAllBuyerOrders(filterRequest);
    }

    @PutMapping("/{id}")
    public BuyerOrderResponse update(@NotNull @Positive @RequestParam Long buyerId,@NotNull @Positive @PathVariable Long id, @Valid @RequestBody BuyerOrderUpdateRequest requestNew) {
        return buyerOrderService.update(buyerId, id, requestNew);
    }

    @DeleteMapping(value = "/{id}",consumes = MediaType.ALL_VALUE)
    public void delete(@NotNull @Positive @RequestParam Long buyerId,@NotNull @Positive @PathVariable Long id) {
        buyerOrderService.delete(buyerId, id);
    }

    @PatchMapping("/{id}/cancel")
    public void cancel(@NotNull @Positive @RequestParam Long buyerId,@NotNull @Positive @PathVariable Long id) {
        buyerOrderService.cancel(buyerId, id);
    }

}
