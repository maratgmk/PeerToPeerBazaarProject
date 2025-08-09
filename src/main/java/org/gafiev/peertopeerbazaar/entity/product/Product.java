package org.gafiev.peertopeerbazaar.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.user.User;

import java.math.BigDecimal;
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
    private Category category;

    /**
     * единица измерения одной порции продукта
     */
    @Enumerated(EnumType.STRING)
    private PortionUnit portionUnit;

    /**
     * weight это вес одной порции продукта
     */
    @Column(name = "weight")
    private Double weightKg;

    /**
     * volume это объём одной порции продукта
     */
    @Column(name = "volume")
    private Double volumeLtr;

    /**
     * price есть цена за порцию продукта
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * imageURI изображение продукта
     */
    @Column(name = "image_uri") //TODO сделать интеграцию с базой фото
    private String imageURI;

    /**
     * qrCode является ссылкой на страницу продукта
     */
    @Column(name = "qr_code")
    private String qrCode; //TODO сделать интеграцию с базой QR

    /**
     * создатель продукта
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private User author;

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
