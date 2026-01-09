package org.gafiev.peertopeerbazaar.entity.product;

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
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * The Product entity outlines the key characteristics of a product.
 * It connects the author (who is also the user) and set of seller offers that include this product.
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = "sellerOfferSet")
@ToString(exclude = "sellerOfferSet")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "product")
public class Product {
    /**
     * Unique product identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Product name.
     */
    @Column(name = "name")
    private String name;

    /**
     * Product description details the time and method of creation.
     */
    @Column(name = "description")
    private String description;

    /**
     * Product category (e.g., type of transportation or storage) as the general characteristics of the product.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    /**
     * Unit of measurement used to count the product quantity.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "portion_unit")
    private PortionUnit portionUnit;

    /**
     * Product weight per portion unit (in kg).
     */
    @Column(name = "weight", precision = 6, scale = 2, nullable = false)
    private BigDecimal weightKg;
    /**
     * Product volume per portion unit (in ltr/liters).
     */
    @Column(name = "volume", precision = 6, scale = 2, nullable = false)
    private BigDecimal volumeLtr;

    /**
     * Product price per portion unit.
     */
    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    /**
     * Image Uniform Resource Identifier (URI) for the product.
     */
    @Column(name = "image_uri")
    private String imageURI;

    /**
     * Quick Response Code (QR code) URL for product information access.
     */
    @Column(name = "qr_code")
    private String qrCode;

    /**
     * Author of product (User).
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private User author;

    /**
     * Timestamp of when the product entity was created/recorded.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Set of SellerOffer entities that include the product.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();

    /**
     * Adds a seller offer to a set of offers that include the product.
     *
     * @param sellerOffer SellerOffer entity.
     */
    public void addSellerOffer(SellerOffer sellerOffer) {
        sellerOfferSet.add(sellerOffer);
        sellerOffer.setProduct(this);
    }

    /**
     * Removes a seller offer to a set of offers that include the product.
     *
     * @param sellerOffer SellerOffer entity.
     */
    public void removeSellerOffer(SellerOffer sellerOffer) {
        sellerOfferSet.remove(sellerOffer);
        sellerOffer.setProduct(null);
    }
}
