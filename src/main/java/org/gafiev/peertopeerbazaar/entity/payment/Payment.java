package org.gafiev.peertopeerbazaar.entity.payment;

import com.neovisionaries.i18n.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность Payment описывает процесс оплаты заказа
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = "buyerOrderSet")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "buyerOrderSet")
@Entity
@Table(name = "payment")
public class Payment {
    /**
     * id есть идентификатор платежа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * amount есть полная сумма платежа за заказ.
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * валюта.
     */
    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

    /**
     * способ оплаты.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    /**
     * состояние платежа.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    /**
     * дата и время подтверждения оплаты.
     */
    @Column(name = "completion_date_time")
    private LocalDateTime completionDateTime;

    /**
     * множество заказов, которое оплачено этим одним платежом,
     * если удалить платеж, то это множество будет сиротой и удалится из БД???
     */
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BuyerOrder> buyerOrderSet = new HashSet<>();

    /**
     * метод добавления заказа покупателя во множество заказов по этому платежу
     * @param buyerOrder заказ покупателя
     */
    public void addBuyerOrder(BuyerOrder buyerOrder){
        buyerOrderSet.add(buyerOrder);
        buyerOrder.setPayment(this);
    }

    /**
     * метод удаления заказа покупателя из множества заказов, если он не оплачен
     * @param buyerOrder заказ покупателя
     */
    public void removeBuyerOrder(BuyerOrder buyerOrder){
        buyerOrderSet.remove(buyerOrder);
        buyerOrder.setPayment(null);
    }

}
