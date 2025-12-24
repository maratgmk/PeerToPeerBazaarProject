package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Address represents the point of the buyer's order delivery and the point of the seller's offer.
 */
@EqualsAndHashCode(exclude = {"sellerOfferSet", "deliverySet"})
@ToString(exclude = {"sellerOfferSet", "deliverySet"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "address")
public class Address {
    /**
     * Unique identifier of Address .
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Name of address's town.
     */
    @Column(name = "town")
    private String town;

    /**
     * Name of address's street.
     */
    @Column(name = "street")
    private String street;

    /**
     * Number of address's building.
     */
    @Column(name = "number_building")
    private Integer buildingNumber;

    /**
     * Post code of address.
     */
    @Column(name = "post_code")
    private Integer postCode;

    /**
     * Value of address's latitude.
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * Value of address's longitude.
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * Value of address's altitude.
     */
    @Column(name = "altitude")
    private Double altitude;

    /**
     * The spatial accuracy of the coordinates (horizontal and vertical, in meters).
     */
    @Column(name = "accuracy")
    private Double accuracy;

    /**
     * Set of all seller offers associated with this Address.
     */
    @OneToMany(mappedBy = "address", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @Builder.Default
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();

    /**
     * Set of all deliveries associated with this Address.
     */
    @OneToMany(mappedBy = "toAddress", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @Builder.Default
    private Set<Delivery> deliverySet = new HashSet<>();

    /**
     * Time of address creation.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Adds the given seller offer to Set.
     *
     * @param sellerOffer Seller offer.
     */
    public void addSellerOffer(@NonNull SellerOffer sellerOffer) {
        sellerOfferSet.add(sellerOffer);
        sellerOffer.setAddress(this);
    }

    /**
     * Removes the given seller offer from Set.
     *
     * @param sellerOffer Seller offer.
     */
    public void removeSellerOffer(@NonNull SellerOffer sellerOffer) {
        sellerOfferSet.remove(sellerOffer);
        sellerOffer.setAddress(null);
    }

    /**
     * Adds the given delivery to Set.
     *
     * @param delivery Delivery of buyer order to Address.
     */
    public void addDelivery(@NonNull Delivery delivery) {
        deliverySet.add(delivery);
        delivery.setToAddress(this);
    }

    /**
     * Removes given Delivery from Set.
     *
     * @param delivery Delivery of buyer order to Address.
     */
    public void removeDelivery(@NonNull Delivery delivery) {
        deliverySet.remove(delivery);
        delivery.setToAddress(null);
    }
}





