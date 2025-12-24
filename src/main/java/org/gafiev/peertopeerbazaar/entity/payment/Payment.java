package org.gafiev.peertopeerbazaar.entity.payment;

import com.neovisionaries.i18n.CurrencyCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The Payment entity represents a specific transaction made through an external payment service.
 * It manages the amount, status, currency, and the associated buyer orders paid by this transaction
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = "buyerOrderSet")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "buyerOrderSet")
@Entity
@Builder(toBuilder = true)
@Table(name = "payment")
public class Payment {
    /**
     * The unique identifier for the payment record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The total monetary value of the transaction.
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * The currency code used for the transaction (ISO 4217).
     */
    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

    /**
     * The method utilized to process the payment (e.g., Credit Card, Crypto).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    /**
     * The payment status as recorded in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    /**
     * The timestamp provided by the external payment provider upon payment completion.
     */
    @Column(name = "completion_date_time")
    private LocalDateTime completionDateTime;

    /**
     * The timestamp when this payment record was created in the database.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Set of buyer orders paid by this transaction.
     */
    @Builder.Default
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BuyerOrder> buyerOrderSet = new HashSet<>();

    /**
     * Associates a buyer order with this payment and sets up the bidirectional relationship.
     *
     * @param buyerOrder BuyerOrder entity to add.
     */
    public void addBuyerOrder(BuyerOrder buyerOrder) {
        buyerOrderSet.add(buyerOrder);
        buyerOrder.setPayment(this);
    }

    /**
     * Removes a buyer order from this payment and breaks the bidirectional relationship.
     *
     * @param buyerOrder BuyerOrder entity.
     */
    public void removeBuyerOrder(BuyerOrder buyerOrder) {
        buyerOrderSet.remove(buyerOrder);
        buyerOrder.setPayment(null);
    }
}
