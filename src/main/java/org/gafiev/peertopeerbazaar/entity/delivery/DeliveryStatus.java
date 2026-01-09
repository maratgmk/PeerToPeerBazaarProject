package org.gafiev.peertopeerbazaar.entity.delivery;

/**
 * Represents the status of a delivery.
 */
public enum DeliveryStatus {
    /**
     * Delivery has been created after payment completion. A drone is not yet assigned.
     */
    CREATED,
    /**
     * A drone has been assigned to the delivery.
     */
    DRONE_ASSIGNED,
    /**
     * Delivery has been successfully completed.
     */
    DELIVERED,
    /**
     * Delivery is currently in progress.
     */
    ON_THE_WAY,
    /**
     * Delivery is delayed and will arrive later than the scheduled time slot.
     */
    DELAYED,
    /**
     * Delivery attempt failed.
     */
    FAILED,
    /**
     * Delivery was cancelled by the buyer.
     */
    CANCELLED_BY_BUYER,
    /**
     * Delivery was cancelled by the seller.
     */
    CANCELLED_BY_SELLER
}

