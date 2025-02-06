package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.*;
import lombok.*;

/**
 * сущность PartOfferToBuy указывает на ту часть предложения продавца, которую хочет заказать покупатель
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "part_offer_to_buy")
public class PartOfferToBuy {
    /**
     * id есть идентификатор объекта класса PartOfferToBuy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * количество порций из предложения продавца, которые хочет заказать покупатель.
     */
    @Column(name = "unit_count")
    private Integer unitCount;

    /**
     * предложение продавца sellerOffer является родительской сущностью для partOfferToBuy.
     * сумма всех partOfferToBuy.getUnitCount одного экземпляра SellerOffer равно unitCount этого экземпляра sellerOffer плюс что осталось.
     * fetch = FetchType.LAZY означает, что при загрузке partOfferToBuy сущность sellerOffer будет загружаться только при специальном дополнительном запросе.
     * по умолчанию будет доступен sellerOfferId, который является внешним ключом к PartOfferToBuy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private SellerOffer sellerOffer;

    /**
     * заказ покупателя (buyerOrder) состоит из множества частей (partOfferToBuy) от разных предложений продавцов (sellerOfferSet).
     * buyerOrder является родительской сущностью для partOfferToBuy.
     * fetch = FetchType.LAZY означает, что при загрузке partOfferToBuy сущность buyerOrderDrone будет загружаться только при специальном дополнительном запросе.
     * по умолчанию будет доступен buyerOrderId, который является внешним ключом к PartOfferToBuy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private BuyerOrder buyerOrder;

    /**
     * корзина покупателя, в которой лежит partOfferToBuySet (множество частей предложений от разных продавцов).
     * fetch = FetchType.LAZY означает, что при загрузке partOfferToBuy корзина будет загружаться только при специальном дополнительном запросе.
     * по умолчанию будет доступен buyerOrderId, который является внешним ключом к PartOfferToBuy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Basket basket;
}
