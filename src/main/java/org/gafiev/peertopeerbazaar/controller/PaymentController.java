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

    @GetMapping(path = "/{id}/complete", consumes = MediaType.ALL_VALUE)
    public PaymentRedirectResponse completePayment(@Valid @PathVariable Long id) {
        return paymentService.completePayment(id);
    }

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public PaymentResponse getPaymentById(@NotNull @Positive @PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping(path = "/{id}/order", consumes = MediaType.ALL_VALUE)
    public PaymentResponse getPaymentByIdWithBuyerOrder(@NotNull @Positive @PathVariable Long id,
                                                        @RequestParam(value = "order", required = false, defaultValue = "false") Boolean isOrder) {
        return isOrder ? paymentService.getPaymentByIdWithBuyerOrder(id) : paymentService.getPaymentById(id);
    }

    @PostMapping("/all")
    public Set<PaymentResponse> getAllPaymentSet(@Valid @RequestBody PaymentFilterRequest filterRequest) {
        return paymentService.getAllPaymentSet(filterRequest);
    }

    @PutMapping("/{id}")
    public PaymentResponse updatePayment(@NotNull @Positive @PathVariable Long id, @Valid @RequestBody PaymentUpdateRequest paymentNew) {
        return paymentService.updatePayment(id, paymentNew);
    }

    @DeleteMapping(value = "/{id}",consumes = MediaType.ALL_VALUE)
    public void deleteById(@NotNull @Positive @PathVariable Long id){
        paymentService.deletePayment(id);
    }
}
