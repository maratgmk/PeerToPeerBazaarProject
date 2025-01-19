package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Сущность Delivery отражает факт доставки заказа от указанного адреса к указанному адресу конкретным дроном
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "delivery")
public class Delivery {
    /**
     * id есть уникальный номер доставки заказа
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * deliveryStatus показывает соcтояние доставки заказа
     */
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    /**
     *  dateTime есть показывает время доставки заказа
     */
    @Column(name = "expected_date_time")
    private LocalDateTime expectedDateTime;

    /**
     * buyerOrder это заказ покупателя, который доставляется от продавца к покупателю,
     * связь - много доставок по одному заказу покупателя
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private BuyerOrder buyerOrder;

    /**
     * Адрес куда доставлять
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Address toAddress;

    /**
     * Адрес откуда забирать
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Address fromAddress;


    /**
     * один drone совершает множество доставок
     */
    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Drone drone;

}
