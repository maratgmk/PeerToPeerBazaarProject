package org.gafiev.peertopeerbazaar.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The SellerOffer entity represents a specific offer made by a seller for a product.
 * It manages the status, timing, associated address, and available product units (parts).
 */
@Slf4j
@EqualsAndHashCode(exclude = {"partOfferToBuyList", "product", "seller", "address"})
@ToString(exclude = {"partOfferToBuyList", "product", "seller", "address"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "seller_offer")
public class SellerOffer {

    /**
     * id Unique seller offer identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Current status of the seller offer.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status")
    private OfferStatus offerStatus;

    /**
     * Special comments or notes related to the seller offer.
     */
    @Column(name = "comment")
    private String comment;

    /**
     * Seller offer start date/time.
     */
    @Column(name = "creation_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime creationDateTime;

    /**
     * Seller offer end date/time.
     */
    @Column(name = "finish_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime finishDateTime;

    /**
     * Timestamp when the SellerOffer entity was first recorded in the database.
     */
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * The product offered by the seller (author).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    /**
     * The seller who created this seller offer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    /**
     * The address from which the product will be picked up.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    /**
     * A collection of PartOfferToBuy entities (parts) available for purchase under this offer.
     */
    @Builder.Default
    @OneToMany(mappedBy = "sellerOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartOfferToBuy> partOfferToBuyList = new ArrayList<>();

    /**
     * Associates a part offer with this seller offer (bidirectional).
     *
     * @param partOfferToBuy The part offer to add.
     */
    public void addPartOfferToBuy(@NonNull PartOfferToBuy partOfferToBuy) {
        partOfferToBuyList.add(partOfferToBuy);
        partOfferToBuy.setSellerOffer(this);
    }

    /**
     * Disassociates a part offer from this seller offer (breaks relationship).
     *
     * @param partOfferToBuy The part offer to remove.
     */
    public void removePartOfferToBuySet(@NonNull PartOfferToBuy partOfferToBuy) {
        partOfferToBuyList.remove(partOfferToBuy);
        partOfferToBuy.setSellerOffer(null);
    }

    /**
     * Calculates the current count of units that are not yet reserved.
     *
     * @return The number of actual available units.
     */
    public int getActualUnitCount() {
        return (int) partOfferToBuyList.stream()
                .filter(part -> part.getStatus().equals(PartOfferToBuyStatus.NOT_RESERVED))
                .count();
    }
}
