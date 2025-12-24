package org.gafiev.peertopeerbazaar.entity.order;


/**
 *  Represents the status of a buyer order.
 */
public enum BuyerOrderStatus {
    /**
     * Order created and waiting for payment.
     */
    CREATED,
    /**
     * Payment confirmed.
     */
    PAID,
    /**
     * Cancelled or rejected.
     */
    DENIED,
    /**
     * Being prepared for shipping.
     */
    PROCESSED,
    /**
     * Waiting for drone pickup.
     */
    READY_FOR_DELIVERY,
    /**
     * In transit via drone.
     */
    ON_THE_WAY,
    /**
     * Successfully received by the buyer.
     */
    DELIVERED
}
