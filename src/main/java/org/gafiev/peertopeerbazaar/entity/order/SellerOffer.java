package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 *  предложение (оффер) продавца
 */

@EqualsAndHashCode(exclude = "partOfferToBuySet")
@ToString(exclude = "partOfferToBuySet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "seller_offer")
public class SellerOffer {

    /**
     * id идентификатор предложения продавца
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * количество единиц измерения в оффере
     */
    @Column(name = "unit_count")
    private Integer unitCount;

    /**
     * состояние оффера
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status")
    private OfferStatus offerStatus;

    /**
     * комментарии к предложению продавца
     */
    @Column(name = "comment")
    private String comment;

    /**
     * время создания оффера
     */
    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime;

    /**
     * время окончания действия оффера
     */
    @Column(name = "finish_date_time")
    private LocalDateTime finishDateTime;

    /**
     * продукт созданный данным продавцом
     * fetch = FetchType.LAZY значит что при загрузке sellerOffer продукт загружаться не будет,
     * но productId по умолчанию доступен, так как это внешний ключ для SellerOffer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    /**
     * продавец - создатель оффера
     * fetch = FetchType.LAZY значит что при загрузке sellerOffer продавец загружаться не будет,
     * но userId по умолчанию доступен, так как это внешний ключ для SellerOffer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    /**
     * адрес, откуда дрон заберёт часть оффера (заказ)
     * fetch = FetchType.LAZY значит что при загрузке sellerOffer адрес загружаться не будет,
     * но addressId по умолчанию доступен, так как это внешний ключ для SellerOffer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    /**
     * partOfferToBuySet множество частей предложения продавца, которые можно выбрать.
     * является владеющей стороной, PartOfferToBuy содержит внешний ключ от SellerOffer,
     * что отражено в mappedBy = "sellerOffer". SellerOffer есть родительская сущность и управляет жизненным циклом PartOfferToBuy,
     * то есть все изменения в SellerOffer каскадом переходят в PartOfferToBuy (cascade = CascadeType.ALL).
     * orphanRemoval = true означает, что при методе partOfferToBuySet.remove(partOfferToBuy),
     * автоматом удаляется partOfferToBuy из БД. Это заслуга Hibernate.
     */
    @OneToMany(mappedBy = "sellerOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * метод добавления заказанной части оффера к множеству всех заказанных частей данного оффера
     * @param partOfferToBuy часть оффера продавца, заказанного покупателем
     */
    public void addPartOfferToBuySet(@NonNull PartOfferToBuy partOfferToBuy){
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.setSellerOffer(this);
    }

    /**
     * метод удаления заказанной части оффера из множества всех заказанных частей данного оффера
     * @param partOfferToBuy часть оффера продавца, заказанного покупателем
     */
    public void removePartOfferToBuySet(@NonNull PartOfferToBuy partOfferToBuy){
        partOfferToBuySet.remove(partOfferToBuy);
        partOfferToBuy.setSellerOffer(null);
    }

    /**
     * метод возвращает остаток единиц товара, которые ещё не заказаны
     * @return количество незаказанных единиц товара
     */
    public int getActualUnitCount(){
        int orderedCount = partOfferToBuySet.stream()
                .filter(part -> part.getBuyerOrder() != null && part.getBuyerOrder().getBuyerOrderStatus() != BuyerOrderStatus.DENIED)
                .mapToInt(PartOfferToBuy::getUnitCount)
                .sum();
        return unitCount - orderedCount;
    }

}
