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
     * id есть идентификатор объекта класса PartOfferToBuy
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Количество порций из предложения продавца, которые хочет заказать покупатель
     */
    @Column(name = "unit_count")
    private Integer unitCount;

    /**
     * предложение продавца
     * сумма всех partOfferToBuy.getPortionCount одного экземпляра SellerOffer равно unitCount этого экземпляра sellerOffer
     * "надо причесать"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private SellerOffer sellerOffer;

    /**
     * заказ покупателя состоит из множества частей от разных sellerOffer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private BuyerOrder buyerOrder;

    /**
     * корзина покупателя, в которой лежит множество частей предложений от разных продавцов
     * параметр FetchType.LAZY означает, что при загрузке partOfferToBuy корзина загружаться не будет
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Basket basket;
}
