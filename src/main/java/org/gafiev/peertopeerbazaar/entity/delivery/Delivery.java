package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;

/**
 * Доставка заказа покупателя от адреса продавца на адрес покупателя конкретным дроном.
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = {"buyerOrder", "drone","toAddress","fromAddress"})
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"buyerOrder", "drone","toAddress","fromAddress"})
@Entity
@Builder
@Table(name = "delivery")
public class Delivery {
    /**
     * идентификатор доставки заказа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * соcтояние доставки заказа.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus;

    /**
     * Временной диапазон доставки заказа.
     * в таблице @Table(name = "delivery") столбец time_slot может указывать на нулевую ссылку.
     */
    @Embedded
    @Column(name = "time_slot",nullable = true)
    private TimeSlot timeSlot;

    /**
     * заказ покупателя.
     * связь - много доставок по одному заказу покупателя.
     * fetch = FetchType.LAZY относится к полю BuyerOrder, что означает, что при загрузке из БД доставки заказ покупателя загружаться не будет.
     * cascade = {CascadeType.MERGE, CascadeType.PERSIST} это относится к полю BuyerOrder, при обновлении и сохранении заказа каскадно будет обновляться связанный заказ покупателя.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private BuyerOrder buyerOrder;

    /**
     * Адрес покупателя.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Address toAddress;

    /**
     * Адрес продавца.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Address fromAddress;

    /**
     * дрон осуществляющий доставку.
     * один drone совершает множество доставок.
     */
    @ManyToOne (fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Drone drone;
}
