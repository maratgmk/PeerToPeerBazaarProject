package org.gafiev.peertopeerbazaar.exception;

import org.gafiev.peertopeerbazaar.dto.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppControllerAdvice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .userFriendlyMessage("Can't find data")
                .sourceMessage(entityNotFoundException.getMessage())
                .build());
    }

    @ExceptionHandler(PaymentStatusException.class)
    public ResponseEntity<ErrorResponse> handlePaymentStatusException(PaymentStatusException paymentStatusException) {
        return ResponseEntity.status(523).body(ErrorResponse.builder()
                .code(523)
                .userFriendlyMessage("Can't complete payment.")
                .sourceMessage(paymentStatusException.getMessage())
                .build());
    }

    @ExceptionHandler(DroneException.class)
    public ResponseEntity<ErrorResponse> handleDroneException(DroneException droneException) {
        return ResponseEntity.status(523).body(ErrorResponse.builder()
                .code(523)
                .userFriendlyMessage("Cannot request a drone for delivery.")
                .sourceMessage(droneException.getMessage())
                .build());
    }

    @ExceptionHandler(ClosedOfferException.class)
    public ResponseEntity<ErrorResponse> handleClosedOfferException(ClosedOfferException closedOfferException) {
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .userFriendlyMessage("These parts are removed from the basket.")
                .sourceMessage(closedOfferException.getMessage())
                .build());
    }

    @ExceptionHandler(IllegalBusinessStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalBusinessStateException(IllegalBusinessStateException illegalBusinessStateException) {
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .code(400)
                .userFriendlyMessage("Business logic is broken.")
                .sourceMessage(illegalBusinessStateException.getMessage())
                .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException runtimeException) {
        return ResponseEntity.status(500).body(ErrorResponse.builder()
                .code(500)
                .userFriendlyMessage("An unexpected error occurred. Please try again later.")
                .sourceMessage(runtimeException.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity.status(500).body(ErrorResponse.builder()
                .code(500)
                .userFriendlyMessage("An unexpected error occurred. Please try again later.")
                .sourceMessage(exception.getMessage())
                .build());
    }

}
//TODO 1. дать ответы на все исключения (мои исключения из папки Exception) приложения,
// надо обработать дополнительно RunTimeException(необрабатываемые исключения) и Exception ()
// 2. проверить ответы для каждого типа исключения через Postman
//


//