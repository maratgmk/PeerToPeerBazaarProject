package org.gafiev.peertopeerbazaar.entity.delivery;

public enum DeliveryStatus {
    /**
     * Доставка создана, когда платеж осуществлен. Дрон не назначен.
     */
    CREATED,
    /**
     * Дрон назначен для доставки.
     */
    DRONE_ASSIGNED,
    /**
     * Доставка завершена успешно.
     */
    DELIVERED,
    /**
     * Доставка в процессе выполнения.
     */
    ON_THE_WAY,
    /**
     * Доставка выполняется, но будет выполнена позже заявленного интервала времени.
     */
    DELAYED,
    /**
     * Доставка провалена.
     */
    FAILED,
    /**
     * Доставка отменена покупателем.
     */
    CANCELLED_BY_BUYER,
    /**
     * Доставка отменена продавцом.
     */
    CANCELLED_BY_SELLER
}

