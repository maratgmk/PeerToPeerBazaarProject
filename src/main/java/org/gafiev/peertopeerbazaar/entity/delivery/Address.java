package org.gafiev.peertopeerbazaar.entity.delivery;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
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
@Builder(toBuilder = true)
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
    @OneToMany(mappedBy = "address", cascade = {CascadeType.PERSIST,CascadeType.MERGE}, orphanRemoval = true)
    @Builder.Default
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();

    /**
     * по одному адресу осуществляется множество доставок
     */
    @OneToMany(mappedBy = "toAddress", cascade = {CascadeType.PERSIST,CascadeType.MERGE}, orphanRemoval = true)
    @Builder.Default
    private Set<Delivery> deliverySet = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * @param sellerOffer это предложение продавца
     * метод addSellerOffer добавляет предложение продавца во множество предложений продавцов, связанных с этим адресом,
     * и в это предложение устанавливает ссылку на этот адрес
     */
    public void addSellerOffer(@NonNull SellerOffer sellerOffer){
        sellerOfferSet.add(sellerOffer);
        sellerOffer.setAddress(this);
    }

    /**
     *  removeSellerOffer удаляет предложение продавца из коллекции Set<SellerOffer>, связанной с этим адресом,
     * и в это удалённое предложение устанавливает нулевую ссылку на этот адрес
     * @param sellerOffer это объект сущности SellerOffer (предложение продавца)
     */
    public void removeSellerOffer(@NonNull SellerOffer sellerOffer){
        sellerOfferSet.remove(sellerOffer);
        sellerOffer.setAddress(null);
    }

    /**
     * @param delivery это доставка заказа
     * метод addDelivery добавляет доставку во множество доставок заказов, осуществляемых по этому адресу,
     * и в эту доставку устанавливает ссылку на этот адрес
     */
    public void addDelivery(@NonNull Delivery delivery){
        deliverySet.add(delivery);
        delivery.setToAddress(this);
    }

    /**
     * @param delivery это объект класса Delivery
     * метод removeDelivery удаляет доставку из множества доставок, осуществляемых по этому адресу,
     * и в эту удалённую доставку устанавливает нулевую ссылку этот адрес
     */
    public void removeDelivery(@NonNull Delivery delivery){
        deliverySet.remove(delivery);
        delivery.setToAddress(null);
    }

}





