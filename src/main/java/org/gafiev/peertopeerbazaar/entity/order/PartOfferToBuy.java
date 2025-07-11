package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * наименьшая часть предложения продавца, которую может заказать покупатель.
 */
@EqualsAndHashCode(exclude = {"basketSet","buyerOrder","sellerOffer"})
@ToString(exclude = {"basketSet","buyerOrder","sellerOffer"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "part_offer_to_buy")
@Builder(toBuilder = true)
public class PartOfferToBuy {
    /**
     * id есть идентификатор части.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * статус части
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private PartOfferToBuyStatus status = PartOfferToBuyStatus.NOT_RESERVED;

    /**
     * предложение продавца является родительской сущностью для partOfferToBuy.
     * fetch = FetchType.LAZY означает, что при загрузке partOfferToBuy сущность sellerOffer будет загружаться только при специальном дополнительном запросе.
     * по умолчанию будет доступен sellerOfferId, который является внешним ключом к PartOfferToBuy.
     */
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private SellerOffer sellerOffer;

    /**
     * заказ покупателя (buyerOrder) состоит из множества частей (partOfferToBuy) от разных предложений продавцов (sellerOfferSet).
     * buyerOrder является родительской сущностью для partOfferToBuy.
     * fetch = FetchType.LAZY означает, что при загрузке partOfferToBuy сущность buyerOrderDrone будет загружаться только при специальном дополнительном запросе.
     * по умолчанию будет доступен buyerOrderId, который является внешним ключом к PartOfferToBuy.
     */
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private BuyerOrder buyerOrder;

    /**
     * корзина покупателя, в которой лежит partOfferToBuySet (множество частей предложений от разных продавцов).
     * fetch = FetchType.LAZY означает, что при загрузке partOfferToBuy корзина будет загружаться только при специальном дополнительном запросе.
     * по умолчанию будет доступен basketIds, который является внешним ключом к PartOfferToBuy.
     */
    @ManyToMany(mappedBy = "partOfferToBuySet", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Builder.Default
    private Set<Basket> basketSet = new HashSet<>();


    public void addBasket(@NonNull Basket basket){
        basketSet.add(basket);
        basket.getPartOfferToBuySet().add(this);
    }

    public void removeBasket(@NonNull Basket basket){
        basketSet.remove(basket);
        basket.getPartOfferToBuySet().remove(this);
    }
}
