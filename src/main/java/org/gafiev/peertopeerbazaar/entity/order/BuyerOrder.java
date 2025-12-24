package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * This entity captures the state, details, and relationships of an order placed by a buyer.
 * The main domain entity representing a complete buyer order.
 * It links together the buyer (user), payment information,
 * delivery details, and the specific parts of seller offer being purchased.
 */
@EqualsAndHashCode(exclude = {"partOfferToBuySet", "deliverySet"})
@ToString(exclude = {"partOfferToBuySet", "deliverySet"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "buyer_order")
public class BuyerOrder {
    /**
     * Unique buyer order identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Current buyer order status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_order_status")
    @Builder.Default
    private BuyerOrderStatus buyerOrderStatus = BuyerOrderStatus.CREATED;

    /**
     * User entity represents buyer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    /**
     * Payment entity representing payment information for this order.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Payment payment;

    /**
     * Set of parts offer to buy associated with choice of buyer.
     */
    @Builder.Default
    @OneToMany(mappedBy = "buyerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * Set of deliveries handles this buyer order.
     */
    @Builder.Default
    @OneToMany(mappedBy = "buyerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delivery> deliverySet = new HashSet<>();

    /*
     * Timestamp of BuyerOrder entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Adds PartOfferToBuy to this BuyerOrder.
     *
     * @param partOfferToBuy Part offer to buy that chosen by buyer.
     */
    public void addPartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.setBuyerOrder(this);
    }

    /**
     * Removes PartOfferToBuy from this BuyerOrder.
     *
     * @param partOfferToBuy Part offer to buy that chosen by buyer.
     */
    public void removePartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.remove(partOfferToBuy);
        partOfferToBuy.setBuyerOrder(null);
    }

    /**
     * Adds Delivery to this BuyerOrder.
     *
     * @param delivery Delivery for this BuyerOrder.
     */
    public void addDelivery(@NonNull Delivery delivery) {
        deliverySet.add(delivery);
        delivery.setBuyerOrder(this);
    }

    /**
     * Removes Delivery from this BuyerOrder.
     *
     * @param delivery Delivery for this BuyerOrder.
     */
    public void removeDelivery(@NonNull Delivery delivery) {
        deliverySet.remove(delivery);
        delivery.setBuyerOrder(null);
    }

    /**
     * Determines total weight of buyer order.
     *
     * @return Weight of buyer order.
     */
    public BigDecimal getWeightKg() {
        return partOfferToBuySet.stream()
                .map(part -> part.getSellerOffer().getProduct().getWeightKg())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Determines total volume of buyer order.
     *
     * @return Volume of buyer order.
     */
    public BigDecimal getVolumeLtr() {
        return partOfferToBuySet.stream()
                .map(part -> part.getSellerOffer().getProduct().getVolumeLtr())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
