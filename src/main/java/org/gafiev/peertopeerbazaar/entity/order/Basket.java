package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность корзина показывает, что выбрал покупатель из разных предложений разных продавцов
 */
@EqualsAndHashCode(exclude = {"partOfferToBuySet","buyer"})
@ToString(exclude = {"partOfferToBuySet","buyer"})
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // это вызывает конфликт с @MapsId????
    @Column(name = "buyer_id")
    private Long id;

    /**
     * buyer это покупатель передаёт свой id корзине
     */
    @OneToOne(fetch = FetchType.LAZY) //, cascade = CascadeType.ALL Cascade должен быть только на owning side (User.basket)
    @MapsId
    private User buyer;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

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
        partOfferToBuy.getBasketSet().remove(this);
    }
}
