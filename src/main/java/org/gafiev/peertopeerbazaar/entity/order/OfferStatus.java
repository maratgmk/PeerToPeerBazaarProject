package org.gafiev.peertopeerbazaar.entity.order;

/**
 * Represents the status of a seller offer.
 */
public enum OfferStatus {

    /**
     * Offer is in the presale stage (advance ordering).
     */
    PRESALE,

    /**
     * Offer is active and open for new orders.
     */
    OPENED,

    /**
     * Offer is finished or all units are sold out.
     */
    CLOSED,

    /**
     * Offer is cancelled by the seller or the system.
     */
    CANCELLED
}
