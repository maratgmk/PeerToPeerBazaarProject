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
 * Продукт, создаётся продавцом (автором) для продажи покупателям.
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
     * id идентификатор продукта
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * название продукта
     */
    @Column(name = "name")
    private String name;

    /**
     * описание продукта
     */
    @Column(name = "description")
    private String description;

    /**
     * обобщенная характеристика продукта
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    /**
     * единица измерения одной порции продукта
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "portion_unit")
    private PortionUnit portionUnit;

    /**
     * weight это вес одной порции продукта
     */
    @Column(name = "weight", precision = 6, scale = 2, nullable = false)
    private BigDecimal weightKg;
    /**
     * volume это объём одной порции продукта
     */
    @Column(name = "volume", precision = 6, scale = 2, nullable = false)
    private BigDecimal volumeLtr;

    /**
     * price есть цена за порцию продукта
     */
    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    /**
     * imageURI изображение продукта
     */
    @Column(name = "image_uri")
    private String imageURI;

    /**
     * qrCode является ссылкой на страницу продукта
     */
    @Column(name = "qr_code")
    private String qrCode;

    /**
     * создатель продукта
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private User author;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * множество предложений продавца, связанное с данным product
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();

    /**
     * добавление предложения продавца во множество предложений, связанных с данным продуктом
     * @param sellerOffer предложение продавца
     */
    public void addSellerOffer(SellerOffer sellerOffer){
        sellerOfferSet.add(sellerOffer);
        sellerOffer.setProduct(this);
    }

    /**
     * удаление предложения продавца из множества предложений, связанное с данным продуктом
     * @param sellerOffer предложение продавца
     */
    public void removeSellerOffer(SellerOffer sellerOffer){
        sellerOfferSet.remove(sellerOffer);
        sellerOffer.setProduct(null);
    }

}
