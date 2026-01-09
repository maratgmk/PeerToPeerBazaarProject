package org.gafiev.peertopeerbazaar.entity.payment;

/**
 * Represents the current status of a payment transaction.
 */
public enum PaymentStatus {

    /** The payment intent has been recorded, but payment has not yet begun or been confirmed. */
    CREATED,

    /** The payment has been successfully authorized and captured. */
    SUCCESS,

    /** The payment is currently being processed by the external gateway or bank. */
    PROCESSING,

    /** The payment was rejected, cancelled, or failed validation. */
    DENIED
}
