package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalPaymentResponse;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.service.model.interfaces.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "callback", consumes = MediaType.ALL_VALUE)
public class ExternalPaymentController {
    private final PaymentService paymentService;

    @PostMapping(path = "/notify/{paymentId}")
    public ResponseEntity<String> getStatus(@NotNull @PathVariable Long paymentId, @RequestBody ExternalPaymentResponse paymentResponse) {
        try {
            PaymentResponse response = paymentService.updatePayment(paymentId, PaymentUpdateRequest.builder()
                    .paymentStatus(paymentResponse.status())
                    .completionDateTime(paymentResponse.completionDateTime())
                    .build());
            log.info("Платеж выполнен: {}", response);
            return new ResponseEntity<>("Обратный вызов принят", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.error("Платеж не найден для ID {}: {}", paymentId, e.getMessage());
            return new ResponseEntity<>("Платеж не найден", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Ошибка при обработке обратного вызова платежа: {}", e.getMessage());
            return new ResponseEntity<>("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
