package org.gafiev.peertopeerbazaar.exception;

/**
 * Exception thrown when errors related to payment status occur.
 * <p>
 * This exception is used in the application's business logic when the current
 * payment status does not allow performing the requested operation (for example,
 * attempting to cancel an already completed payment or re-paying an already
 * processed order).
 * <p>
 * Since this class extends RuntimeException, it is an
 * unchecked exception and does not require mandatory handling.
 */
public class PaymentStatusException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message, which can later be retrieved
     *                using the getMessage() method.
     */
    public PaymentStatusException(String message) {
        super(message);
    }
}
