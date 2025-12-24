package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "BuyerOrders", description = "Provides endpoints for managing buyer orders.")
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

    @Operation(summary = "Creates a buyer order", description = "Allows buyer to create order.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @PostMapping("/create")
    public Set<BuyerOrderResponse> create(@NotNull @Positive @RequestParam Long buyerId,
                                          @Parameter(description = "Data for buyer order creation.", required = true)
                                          @Valid @RequestBody BuyerOrderCreateRequest candidate) {
        return buyerOrderService.create(buyerId, candidate);
    }

    @Operation(summary = "Retrieves a buyer order by its ID.", description = "Retrieves a specific order belonging to the buyer.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @GetMapping(path = "/{id}/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public BuyerOrderResponse get(@NotNull @Positive @PathVariable Long id,
                                  @NotNull @Positive @PathVariable Long buyerId) {
        return buyerOrderService.get(id, buyerId);
    }

    @Operation(summary = "Retrieves buyer orders by status.",
            description = "Retrieves all orders for a specific buyer filtered by order status.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @PostMapping(value = "/status", consumes = MediaType.ALL_VALUE)
    public Set<BuyerOrderResponse> getAllByStatus(@NotNull @Positive @RequestParam Long buyerId,
                                                  @NotNull @RequestParam BuyerOrderStatus buyerOrderStatus) {
        return buyerOrderService.getAllByStatus(buyerId, buyerOrderStatus);
    }

    @Operation(summary = "Retrieves set of buyer order by filter criteria.",
            description = "Allows users with the ADMIN role to retrieve buyer orders using specific filter criteria.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public Set<BuyerOrderResponse> getAllBuyerOrders(
            @Parameter(description = "Data for the specific filter to get all interested buyer orders.", required = true)
            @Valid @RequestBody BuyerOrderFilterRequest filterRequest) {
        return buyerOrderService.getAllBuyerOrders(filterRequest);
    }

    @Operation(summary = "Updates a buyer order.", description = "Allows the buyer to update an existing order with new parameters.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication )")
    @PutMapping("/{id}/user/{buyerId}")
    public BuyerOrderResponse update(@NotNull @Positive @PathVariable Long buyerId,
                                     @NotNull @Positive @PathVariable Long id,
                                     @Parameter(description = "Data for updating the buyer order.", required = true)
                                     @Valid @RequestBody BuyerOrderUpdateRequest requestNew) {
        return buyerOrderService.update(buyerId, id, requestNew);
    }

    @Operation(summary = "Deletes a buyer order.", description = "Allows users with the ADMIN role to delete the buyer order by its ID.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
    public void delete(@NotNull @Positive @PathVariable Long id) {
        buyerOrderService.delete(id);
    }

    @Operation(summary = "Cancels a buyer order.", description = "Allows the buyer to cancel his order.")
    @PreAuthorize("@authz.isSelf(#buyerId, authentication)")
    @PatchMapping("/cancel/{id}")
    public void cancel(@NotNull @Positive @RequestParam Long buyerId,
                       @NotNull @Positive @PathVariable Long id) {
        buyerOrderService.cancel(buyerId, id);
    }
}
