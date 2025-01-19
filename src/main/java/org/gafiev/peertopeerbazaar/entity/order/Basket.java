package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.user.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Сущность корзина показывает, что выбрал покупатель из разных предложений разных продавцов
 */
@EqualsAndHashCode(exclude = "partOfferToBuySet")
@ToString(exclude = "partOfferToBuySet")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Basket {
    /**
     * id уникальный идентификатор корзины, который совпадает с id покупателя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_id")
    private Long id;

    /**
     * buyer это покупатель передаёт свой id корзине
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    private User buyer;

    /**
     * Множество частей офферов всех продавцов, которые выбрал покупатель и положил в корзину,
     * partOfferToBuy это части любых офферов от любых продавцов
     * параметр FetchType.LAZY означает, что при загрузке корзины partOfferToBuySet загружаться не будет
     * чтобы это обойти создаётся кастомный метод в репозитории с помощью JPQL запроса
     */
    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * Метод добавления partOfferToBuy во множество partOfferToBuySet
     *
     * @param partOfferToBuy выбранная покупателем часть предложения продавца
     */
    public void addPartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.setBasket(this);
    }

    /**
     * Метод удаления partOfferToBuy из множества partOfferToBuySet
     *
     * @param partOfferToBuy выбранная покупателем часть предложения продавца
     */
    public void removePartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.remove(partOfferToBuy);
        partOfferToBuy.setBasket(null);
    }
}
