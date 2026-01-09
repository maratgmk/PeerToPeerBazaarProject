package org.gafiev.peertopeerbazaar.exception;

import lombok.Getter;

import java.util.Set;

/**
 * Exception thrown when an attempt is made to interact with or purchase offers
 * that have already been closed.
 *
 * This exception carries a payload of IDs representing the specific offer parts
 * that caused the violation. It is typically used in shopping cart or checkout
 * processes to notify the user that certain items are no longer available and
 * have been automatically removed from their basket.
 *
 * Extends RuntimeException, making it an unchecked exception
 * suitable for modern business logic layers.
 */
@Getter
public class ClosedOfferException extends RuntimeException {

    /**
     * The set of identifiers for the offer parts that are closed and
     * should be removed from the user's basket.
     */
    private final Set<Long> partOfferToBuyIds;

    /**
     * Constructs a new exception with a predefined message containing
     * the IDs of the closed offer parts.
     *
     * @param partOfferToBuyIds a Set of IDs of the unavailable offer parts.
     */
    public ClosedOfferException(Set<Long> partOfferToBuyIds) {
        super("Parts of closed offers: " + partOfferToBuyIds + ". These parts are removed from the basket");
        this.partOfferToBuyIds = partOfferToBuyIds;
    }
}
