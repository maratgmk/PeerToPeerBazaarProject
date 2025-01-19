package org.gafiev.peertopeerbazaar.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.user.User;

import java.util.HashSet;
import java.util.Set;

/**
 * сущность BuyerOrder есть заказ покупателя
 */
@EqualsAndHashCode(exclude = {"partOfferToBuySet","deliverySet"})
@ToString(exclude = {"partOfferToBuySet","deliverySet"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BuyerOrder {
    /**
     * id это идентификатор заказа покупателя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * состояние заказа покупателя
     */
    @Enumerated(EnumType.STRING)
    private BuyerOrderStatus buyerOrderStatus;

    /**
     * buyer покупатель, который осуществляет заказ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;


    /**
     * payment есть платёж по заказу покупателя,
     * в заказе много partOfferToBuySet, оплата по каждому partOfferToBuy ?????
     * payment поместить в PartOfferToBuy ?????
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Payment payment;

    /**
     * partOfferToBuySet есть множество частей предложений от одного продавца, которые покупатель заказал.
     * части partOfferToBuy могут относится к разным офферам одного и того же продавца,
     * при условии, что адреса офферов одинаковые, (чтобы дрон собирал все части по одному адресу)
     */
    @OneToMany(mappedBy = "buyerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * deliverySet есть множество доставок по одному заказу покупателя,
     * настройка mappedBy = "buyerOrder" означает, что в Delivery есть поле buyerOrder,
     *
     */
    @OneToMany(mappedBy = "buyerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delivery> deliverySet = new HashSet<>();

    /**
     * метод добавления partOfferToBuy во множество частей предложений продавцов partOfferToBuySet, которое покупатель сформировал.
     * @param partOfferToBuy часть предложения продавца, которое заказал покупатель
     */
    public void addPartOfferToBuy(PartOfferToBuy partOfferToBuy){
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.setBuyerOrder(this);
    }

    /**
     * метод удаления partOfferToBuy из множества частей предложений продавцов partOfferToBuySet, которое покупатель сформировал.
     * @param partOfferToBuy часть предложения продавца, которое заказал покупатель
     */
    public void removePartOfferToBuy(PartOfferToBuy partOfferToBuy){
        partOfferToBuySet.remove(partOfferToBuy);
        partOfferToBuy.setBuyerOrder(null);
    }

    /**
     * @param delivery есть доставка заказа,
     * метод addDelivery добавляет доставку во множество доставок к данному заказу
     */
    public void addDelivery(@NonNull Delivery delivery) {
        deliverySet.add(delivery);
        delivery.setBuyerOrder(this);
    }

    /**
     * @param delivery есть доставка заказа,
     * метод removeOrderProduct удаляет продукт из множества продуктов в заказе.
     * orderProduct.setSellerOffer(null) означает удаление ордера,
     * что вызовет автоматически orphanRemoval = true
     */
    public void removeDelivery(@NonNull Delivery delivery) {
        deliverySet.remove(delivery);
        delivery.setBuyerOrder(null);
    }
}
