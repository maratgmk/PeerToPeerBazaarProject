package org.gafiev.peertopeerbazaar.exception;

import org.gafiev.peertopeerbazaar.dto.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * Global exception handler for the application's REST controllers.
 *
 * This class intercepts various exceptions thrown by service and controller layers
 * and transforms them into standardized ErrorResponse objects with
 * appropriate HTTP status codes.
 *
 * It ensures that the client receives a clean, user-friendly message while
 * preserving the original error details in the  sourceMessage field
 * for debugging purposes.
 */
@RestControllerAdvice
public class AppControllerAdvice {

    /**
     * Handles cases where a requested resource (entity) does not exist.
     * @return 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .userFriendlyMessage("Can't find data")
                .sourceMessage(e.getMessage())
                .build());
    }

    /**
     * Handles business logic errors related to payment processing.
     * @return 523 Unofficial / Custom Status
     */
    @ExceptionHandler(PaymentStatusException.class)
    public ResponseEntity<ErrorResponse> handlePaymentStatusException(PaymentStatusException e) {
        return ResponseEntity.status(523).body(ErrorResponse.builder()
                .code(523)
                .userFriendlyMessage("Can't complete payment.")
                .sourceMessage(e.getMessage())
                .build());
    }

    /**
     * Handles failures in drone delivery and communication.
     * @return 523 Unofficial / Custom Status
     */
    @ExceptionHandler(DroneException.class)
    public ResponseEntity<ErrorResponse> handleDroneException(DroneException e) {
        return ResponseEntity.status(523).body(ErrorResponse.builder()
                .code(523)
                .userFriendlyMessage("Cannot request a drone for delivery.")
                .sourceMessage(e.getMessage())
                .build());
    }

    /**
     * Handles scenarios where items in the basket are no longer available.
     * @return 404 Not Found
     */
    @ExceptionHandler(ClosedOfferException.class)
    public ResponseEntity<ErrorResponse> handleClosedOfferException(ClosedOfferException e) {
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .userFriendlyMessage("These parts are removed from the basket.")
                .sourceMessage(e.getMessage())
                .build());
    }

    /**
     * Handles generic business rule violations.
     * @return 400 Bad Request
     */
    @ExceptionHandler(IllegalBusinessStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalBusinessStateException(IllegalBusinessStateException e) {
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .code(400)
                .userFriendlyMessage("Business logic is broken.")
                .sourceMessage(e.getMessage())
                .build());
    }

    /**
     * Fallback handler for unhandled runtime exceptions.
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(500).body(ErrorResponse.builder()
                .code(500)
                .userFriendlyMessage("An unexpected error occurred. Please try again later.")
                .sourceMessage(e.getMessage())
                .build());
    }

    /**
     * Catch-all handler for any checked exceptions that were not handled elsewhere.
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(ErrorResponse.builder()
                .code(500)
                .userFriendlyMessage("An unexpected error occurred. Please try again later.")
                .sourceMessage(e.getMessage())
                .build());
    }
}


