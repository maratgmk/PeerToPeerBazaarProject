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
@Table(name = "basket")
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
     * Множество частей офферов всех продавцов, которые выбрал покупатель и положил в корзину.
     * partOfferToBuy это часть любого оффера от любого продавца.
     * параметр FetchType.LAZY означает, что при загрузке корзины partOfferToBuySet загружаться не будет
     * чтобы это обойти создаётся кастомный метод в репозитории с помощью JPQL запроса
     */
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "basket_part",
            joinColumns = @JoinColumn(name = "buyer_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * Метод добавления части предложения в корзину.
     *
     * @param partOfferToBuy выбранная покупателем часть предложения продавца
     */
    public void addPartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.getBasketSet().add(this);
    }

    /**
     * Метод удаления части предложения из корзины.
     *
     * @param partOfferToBuy выбранная покупателем часть предложения продавца
     */
    public void removePartOfferToBuy(PartOfferToBuy partOfferToBuy) {
        partOfferToBuySet.removeIf(partOfferToBuy::equals);
       // partOfferToBuySet.remove(partOfferToBuy);
        partOfferToBuy.getBasketSet().remove(this);
    }
}
