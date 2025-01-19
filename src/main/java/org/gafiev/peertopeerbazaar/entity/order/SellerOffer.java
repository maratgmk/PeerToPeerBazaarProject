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
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    /**
     * продавец - создатель оффера
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    /**
     * адрес, откуда дрон заберёт часть оффера (заказ)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    /**
     * множество частей предложения продавца, которые можно выбрать
     */
    @OneToMany(mappedBy = "sellerOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * метод добавления заказанной части оффера к множеству всех заказанных частей данного оффера
     * @param partOfferToBuy часть оффера продавца, заказанного покупателем
     */
    public void addPartOfferToBuySet(PartOfferToBuy partOfferToBuy){
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.setSellerOffer(this);
    }

    /**
     * метод удаления заказанной части оффера из множества всех заказанных частей данного оффера
     * @param partOfferToBuy часть оффера продавца, заказанного покупателем
     */
    public void removePartOfferToBuySet(PartOfferToBuy partOfferToBuy){
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
