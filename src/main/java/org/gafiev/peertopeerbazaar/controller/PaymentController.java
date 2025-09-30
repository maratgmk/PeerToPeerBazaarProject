package org.gafiev.peertopeerbazaar.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(
        path = "payment",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/complete/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public PaymentRedirectResponse completePayment(@Valid @PathVariable Long id,@Valid @PathVariable Long buyerId) {
        return paymentService.completePayment(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public PaymentResponse getPaymentById(@NotNull @Positive @PathVariable Long id,@Valid @PathVariable Long buyerId) {
        return paymentService.getPaymentById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @GetMapping(path = "/{id}/order/user/{buyerId}", consumes = MediaType.ALL_VALUE)
    public PaymentResponse getPaymentByIdWithBuyerOrders(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long buyerId,
                                                         @RequestParam(value = "order", required = false, defaultValue = "false") Boolean isOrder) {
        return isOrder ? paymentService.getPaymentByIdWithBuyerOrders(id) : paymentService.getPaymentById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Set<PaymentResponse> getAllPaymentSet(@Valid @RequestBody PaymentFilterRequest filterRequest) {
        return paymentService.getAllPaymentSet(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @PutMapping("/{id}/user/{buyerId}")
    public PaymentResponse updatePayment(@NotNull @Positive @PathVariable Long id,
                                         @NotNull @Positive @PathVariable Long buyerId,
                                         @Valid @RequestBody PaymentUpdateRequest paymentNew) {
        return paymentService.updatePayment(id, paymentNew);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#buyerId, authentication)")
    @DeleteMapping(value = "/{id}/user/{buyerId}",consumes = MediaType.ALL_VALUE)
    public void deleteById(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long buyerId){
        paymentService.deletePayment(id);
    }
}
