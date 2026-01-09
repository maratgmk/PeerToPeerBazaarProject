package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentRedirectResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.PaymentService;
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

@Tag(name = "Payments", description = "Operations for managing payment transactions and history.")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(
        path = "payment",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Completes a payment",
            description = "Finalizes the payment process and redirects the buyer to the external payment provider.")
    @PreAuthorize("@authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/complete/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public PaymentRedirectResponse completePayment(@Valid @PathVariable Long id,@Valid @PathVariable Long buyerId) {
        return paymentService.completePayment(id);
    }

    @Operation(summary = "Get payment by ID", description = "Retrieves details of a specific payment using its unique identifier.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public PaymentResponse getPaymentById(@NotNull @Positive @PathVariable Long id,@Valid @PathVariable Long buyerId) {
        return paymentService.getPaymentById(id);
    }

    @Operation(summary = "Get payment with orders",
            description = "Retrieves a specific payment by ID, optionally including associated buyer orders.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/order/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public PaymentResponse getPaymentByIdWithBuyerOrders(@NotNull @Positive @PathVariable Long id,
                                                         @NotNull @Positive @PathVariable Long buyerId,
                                                         @RequestParam(value = "order", required = false,
                                                                 defaultValue = "false") Boolean isOrder) {
        return isOrder ? paymentService.getPaymentByIdWithBuyerOrders(id) : paymentService.getPaymentById(id);
    }

    @Operation(summary = "Filter payments",
             description = "Retrieves a list of payments matching the specified filter criteria. Access is restricted to ADMIN users.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Set<PaymentResponse> getAllPaymentSet(
            @Parameter(description = "Data for the specific filter criteria.", required = true)
            @Valid @RequestBody PaymentFilterRequest filterRequest) {
        return paymentService.getAllPaymentSet(filterRequest);
    }

    @Operation(summary = "Update a payment",
            description = "Allows a buyer or admin to update an existing payment that has not yet been completed.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @PutMapping("/{id}/user/{buyerId}")
    public PaymentResponse updatePayment(@NotNull @Positive @PathVariable Long id,
                                         @NotNull @Positive @PathVariable Long buyerId,
                                         @Parameter(description = "Data for the payment updating.", required = true)
                                         @Valid @RequestBody PaymentUpdateRequest paymentNew) {
        return paymentService.updatePayment(id, paymentNew);
    }

    @Operation(summary = "Deletes a payment",
            description = "Removes a payment record from the database.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @DeleteMapping(value = "/{id}/user/{buyerId}",consumes = MediaType.ALL_VALUE)
    public void deleteById(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long buyerId){
        paymentService.deletePayment(id);
    }
}
