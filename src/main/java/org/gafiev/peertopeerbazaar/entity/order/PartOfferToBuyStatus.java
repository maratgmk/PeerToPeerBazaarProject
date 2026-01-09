package org.gafiev.peertopeerbazaar.entity.order;

/**
 * Represents the reservation status of a specific part within an offer.
 */
public enum PartOfferToBuyStatus {

    /**
     * The part is reserved for a buyer order and is no longer available for others.
     */
    RESERVED,

    /**
     * The part is available and has not been assigned to any order.
     */
    NOT_RESERVED
}
