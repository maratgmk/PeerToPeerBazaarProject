package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Basket entity associated with User entity.
 * Represents parts within the basket selected from a seller's offer.
 */
@EqualsAndHashCode(exclude = {"partOfferToBuySet", "buyer"})
@ToString(exclude = {"partOfferToBuySet", "buyer"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "basket")
public class Basket {
    /**
     * Unique basket identifier, shared with the User ID.
     */
    @Id
    @Column(name = "buyer_id")
    private Long id;

    /**
     * The owner of this basket.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User buyer;

    /**
     * Timestamp when the basket was created.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Set of parts currently held in this basket.
     */
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "basket_part",
            joinColumns = @JoinColumn(name = "buyer_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * Adds the part to the set of parts contained within this basket
     * and ensures bidirectional consistency.
     *
     * A single part unit can simultaneously belong to the part sets of multiple baskets.
     *
     * @param partOfferToBuy The part to be added to this basket's associated set.
     */
    public void addPartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.getBasketSet().add(this);
    }

    /**
     * Removes the part from the set of parts contained within this basket
     * and breaks the bidirectional relationship.
     *
     * @param partOfferToBuy The part to be removed from this basket.
     */
    public void removePartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.removeIf(partOfferToBuy::equals);
        partOfferToBuy.getBasketSet().remove(this);
    }
}
