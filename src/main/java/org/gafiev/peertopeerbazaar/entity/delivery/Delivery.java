package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;

import java.time.LocalDateTime;

/**
 * Сущность Delivery отражает факт доставки заказа от указанного адреса к другому указанному адресу конкретным дроном
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
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
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus;

    /**
     *  expectedDateTime показывает ожидаемое время доставки заказа
     */
    @Column(name = "expected_date_time")
    private LocalDateTime expectedDateTime;

    /**
     * buyerOrder 
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
