package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;

import java.util.HashSet;
import java.util.Set;

/**
 * Сущность Address представляет адрес забора заказа или доставки заказа,
 * в случае отсутствия почтового адреса необходимо пользоваться координатами на местности
 */
@EqualsAndHashCode(exclude = {"sellerOfferSet","deliverySet"})
@ToString(exclude = {"sellerOfferSet","deliverySet"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    /**
     * Поле id есть уникальный идентификатор адреса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * town название населённого пункта
     */
    @Column(name = "town")
    private String town;

    /**
     * street имя улицы
     */
    @Column(name = "street")
    private String street;

    /**
     *  numberBuilding номер дома
     */
    @Column(name = "number_building")
    private Integer numberBuilding;

    /**
     *  zipCode это почтовый индекс
     */
    @Column(name = "zip_code")
    private Integer zipCode;

    /**
     *  latitude это широта координаты адреса
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     *  longitude это долгота координаты адреса
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     *  attitude это высота координаты адреса
     */
    @Column(name = "attitude")
    private Double attitude;

    /**
     * accuracy погрешность измерения координат адреса
     */
    @Column(name = "accuracy")
    private Double accuracy;

    /**
     * с одного адреса продавец предлагает множество заказов
     */
    @OneToMany(mappedBy = "seller", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();

    /**
     * по одному адресу осуществляется множество доставок
     */
    @OneToMany(mappedBy = "toAddress", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<Delivery> deliverySet = new HashSet<>();

    /**
     * @param sellerOffer это предложение продавца
     * метод addSellerOffer добавляет предложение продавца во множество предложений продавцов, связанных с этим адресом,
     * и в это предложение устанавливает ссылку на этот адрес
     */
    public void addSellerOffer(@NonNull SellerOffer sellerOffer){
        this.sellerOfferSet.add(sellerOffer);
        sellerOffer.setAddress(this);
    }

    /**
     *  removeSellerOffer удаляет предложение продавца из коллекции Set<SellerOffer>, связанной с этим адресом,
     * и в это удалённое предложение устанавливает нулевую ссылку на этот адрес
     * @param sellerOffer это объект сущности SellerOffer (предложение продавца)
     */
    public void removeSellerOffer(@NonNull SellerOffer sellerOffer){
        this.sellerOfferSet.remove(sellerOffer);
        sellerOffer.setAddress(null);
    }

    /**
     * @param delivery это доставка заказа
     * метод addDelivery добавляет доставку во множество доставок заказов, осуществляемых по этому адресу,
     * и в эту доставку устанавливает ссылку на этот адрес
     */
    public void addDelivery(@NonNull Delivery delivery){
        this.deliverySet.add(delivery);
        delivery.setToAddress(this);
    }

    /**
     * @param delivery это объект класса Delivery
     * метод removeDelivery удаляет доставку из множества доставок, осуществляемых по этому адресу,
     * и в эту удалённую доставку устанавливает нулевую ссылку этот адрес
     */
    public void removeDelivery(@NonNull Delivery delivery){
        this.deliverySet.remove(delivery);
        delivery.setToAddress(null);
    }

}

// TODO в сервисе оформления заказа должна быть проверка в виде отдельной интеграции,
//  что товар можно доставить по адресу, по координатам, что эти координаты не являются запретной зоной

