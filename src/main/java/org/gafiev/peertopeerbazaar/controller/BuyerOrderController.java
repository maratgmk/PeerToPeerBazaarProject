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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        path = "buyerOrder",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BuyerOrderController {
    private final BuyerOrderService buyerOrderService;

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @PostMapping("/create")
    public Set<BuyerOrderResponse> create(@NotNull @Positive @RequestParam Long buyerId, @Valid @RequestBody BuyerOrderCreateRequest candidate) {
        return buyerOrderService.create(buyerId, candidate);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @GetMapping(path = "/{id}/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public BuyerOrderResponse get(@NotNull @Positive @PathVariable Long id,
                                  @NotNull @Positive @PathVariable Long buyerId) {
        return buyerOrderService.get(id,buyerId);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @PostMapping(value = "/status", consumes = MediaType.ALL_VALUE)
    public Set<BuyerOrderResponse> getAllByStatus(@NotNull @Positive @RequestParam Long buyerId, @NotNull @RequestParam BuyerOrderStatus buyerOrderStatus) {
        return buyerOrderService.getAllByStatus(buyerId, buyerOrderStatus);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public Set<BuyerOrderResponse> getAllBuyerOrders(@Valid @RequestBody BuyerOrderFilterRequest filterRequest) {
        return buyerOrderService.getAllBuyerOrders(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @PutMapping("/{id}/user/{buyerId}")
    public BuyerOrderResponse update(@NotNull @Positive @PathVariable Long buyerId,
                                     @NotNull @Positive @PathVariable Long id,
                                     @Valid @RequestBody BuyerOrderUpdateRequest requestNew) {
        return buyerOrderService.update(buyerId, id, requestNew);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
    public void delete(@NotNull @Positive @PathVariable Long id) {
        buyerOrderService.delete( id);
    }

    @PreAuthorize("@authz.isSelf(#buyerId, authentication)")
    @PatchMapping("/cancel/{id}")
    public void cancel(@NotNull @Positive @RequestParam Long buyerId,
                       @NotNull @Positive @PathVariable Long id) {
        buyerOrderService.cancel(buyerId, id);
    }

}
