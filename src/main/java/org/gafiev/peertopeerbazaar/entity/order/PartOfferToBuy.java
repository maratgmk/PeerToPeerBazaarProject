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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a specific part within the trade system.
 * This entity is managed as an integral part of larger business flows
 * (offering, shopping, and ordering) and does not have its own independent API.
 * It tracks the lifecycle of a part from its initial offer by a seller
 * to being placed in baskets and eventually ordered.
 */
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "status"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "part_offer_to_buy")
@Builder(toBuilder = true)
public class PartOfferToBuy {
    /**
     * Unique part identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Current status of part.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private PartOfferToBuyStatus status = PartOfferToBuyStatus.NOT_RESERVED;

    /**
     * TimeStamp when the PartOfferToBuy entity was first created.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * The seller offer that includes this part.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SellerOffer sellerOffer;

    /**
     * The buyer order that includes this part.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BuyerOrder buyerOrder;

    /**
     * The set of baskets that currently contain this specific part.
     */
    @ManyToMany(mappedBy = "partOfferToBuySet", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Builder.Default
    private Set<Basket> basketSet = new HashSet<>();


    /**
     * Adds a basket to the set of baskets that contain this specific part
     * and ensures bidirectional consistency.
     * This allows the same part unit to be associated with multiple baskets simultaneously.
     *
     * @param basket The basket entity to be added to the collection.
     */
    public void addBasket(@NonNull Basket basket) {
        basketSet.add(basket);
        basket.getPartOfferToBuySet().add(this);
    }

    /**
     * Removes the specified basket from the set of baskets associated with this part.
     * Breaks the bidirectional relationship.
     *
     * @param basket The basket entity to remove.
     */
    public void removeBasket(@NonNull Basket basket) {
        basketSet.remove(basket);
        basket.getPartOfferToBuySet().remove(this);
    }
}
