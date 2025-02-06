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
@Builder
@Table(name = "buyer_order")
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
    @Column(name = "buyer_order_status")
    private BuyerOrderStatus buyerOrderStatus;

    /**
     * buyer покупатель, который осуществляет заказ.
     * BuyerOrder является владеющей стороной, владеет внешним ключом покупателя.
     * fetch = FetchType.LAZY означает, что при вызове BuyerOrder из БД
     * buyer загружаться не будет без дополнительного запроса
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;


    /**
     * payment есть платёж по заказу покупателя.
     * BuyerOrder является владеющей стороной, владеет внешним ключом платежа.
     * fetch = FetchType.LAZY означает, что при вызове BuyerOrder из БД
     * payment загружаться не будет без дополнительного запроса
     * CascadeType.MERGE, CascadeType.PERSIST при всех изменениях в BuyerOrder
     * каскадно произойдут изменения в платеже
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Payment payment;

    /**
     * partOfferToBuySet есть множество частей предложений от разных продавцов, которые покупатель заказал.
     *
     * части partOfferToBuy могут относится к разным офферам одного и того же продавца,
     * при условии, что адреса офферов одинаковые, (чтобы дрон собирал все части по одному адресу)
     * mappedBy = "buyerOrder" означает, что в PartOfferToBuy есть поле buyerOrder, которое является обратной стороной,
     * передавая внешний ключ buyerOrderId владеющей стороне PartOfferToBuy.
     * cascade = CascadeType.ALL означает, что при всех изменениях в BuyerOrder,
     * каскадно произойдут изменения в partOfferToBuySet.
     * orphanRemoval = true означает, что при применении метода partOfferToBuySet.remove(partOfferToBuy),
     * также удалится из БД partOfferToBuy
     */
    @Builder.Default
    @OneToMany(mappedBy = "buyerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartOfferToBuy> partOfferToBuySet = new HashSet<>();

    /**
     * deliverySet есть множество доставок по одному заказу покупателя.
     *
     * mappedBy = "buyerOrder" означает, что в Delivery есть поле buyerOrder, которое является обратной стороной,
     * передавая внешний ключ buyerOrderId владеющей стороне Delivery.
     * cascade = CascadeType.ALL означает, что при всех изменениях в BuyerOrder,
     * каскадно произойдут изменения в deliverySet.
     * orphanRemoval = true означает, что при применении метода deliverySet.remove(delivery),
     * также удалится из БД delivery
     */
    @Builder.Default
    @OneToMany(mappedBy = "buyerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delivery> deliverySet = new HashSet<>();

    /**
     * метод добавления partOfferToBuy во множество частей предложений продавцов partOfferToBuySet, которое покупатель сформировал.
     *
     * @param partOfferToBuy часть предложения продавца, которое заказал покупатель
     */
    public void addPartOfferToBuy(PartOfferToBuy partOfferToBuy){
        partOfferToBuySet.add(partOfferToBuy);
        partOfferToBuy.setBuyerOrder(this);
    }

    /**
     * метод удаления partOfferToBuy из множества частей предложений продавцов partOfferToBuySet, которое покупатель сформировал.
     *
     * @param partOfferToBuy часть предложения продавца, которое заказал покупатель
     */
    public void removePartOfferToBuy(PartOfferToBuy partOfferToBuy){
        partOfferToBuySet.remove(partOfferToBuy);
        partOfferToBuy.setBuyerOrder(null);
    }

    /**
     * метод addDelivery добавляет доставку во множество доставок к данному заказу.
     *
     * @param delivery есть доставка заказа.
     */
    public void addDelivery(@NonNull Delivery delivery) {
        deliverySet.add(delivery);
        delivery.setBuyerOrder(this);
    }

    /**
     * метод removeOrderProduct удаляет продукт из множества продуктов в заказе.
     *
     * @param delivery есть доставка заказа.
     */
    public void removeDelivery(@NonNull Delivery delivery) {
        deliverySet.remove(delivery);
        delivery.setBuyerOrder(null);
    }

    /**
     * получение общего веса заказа в кг
     * @return веса заказа
     */
    public double getWeightKg(){
        return partOfferToBuySet.stream()
                .mapToDouble(part -> part.getUnitCount()*part.getSellerOffer().getProduct().getWeightKg())
                .sum();
    }

    /**
     * получение общего объема заказа в литрах
     * @return объема заказа
     */
    public double getVolumeLtr(){
        return partOfferToBuySet.stream()
                .mapToDouble(part -> part.getUnitCount()*part.getSellerOffer().getProduct().getVolumeLtr())
                .sum();
    }
}
