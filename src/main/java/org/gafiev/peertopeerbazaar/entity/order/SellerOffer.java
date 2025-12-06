package org.gafiev.peertopeerbazaar.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * предложение (оффер) продавца
 */
@Slf4j
@EqualsAndHashCode(exclude ={ "partOfferToBuyList","product","seller","address"})
@ToString(exclude = {"partOfferToBuyList","product","seller","address"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "seller_offer")
public class SellerOffer {

    /**
     * id идентификатор предложения продавца
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * состояние оффера
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status")
    private OfferStatus offerStatus;

    /**
     * комментарии к предложению продавца
     */
    @Column(name = "comment")
    private String comment;

    /**
     * время создания оффера
     */
    @Column(name = "creation_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime creationDateTime;

    /**
     * время окончания действия оффера
     */
    @Column(name = "finish_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime finishDateTime;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * продукт созданный данным продавцом
     * fetch = FetchType.LAZY значит что при загрузке sellerOffer продукт загружаться не будет,
     * но productId по умолчанию доступен, так как это внешний ключ для SellerOffer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    /**
     * продавец - создатель оффера
     * fetch = FetchType.LAZY значит что при загрузке sellerOffer продавец загружаться не будет,
     * но userId по умолчанию доступен, так как это внешний ключ для SellerOffer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    /**
     * адрес, откуда дрон заберёт часть оффера (заказ)
     * fetch = FetchType.LAZY значит что при загрузке sellerOffer адрес загружаться не будет,
     * но addressId по умолчанию доступен, так как это внешний ключ для SellerOffer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    /**
     * partOfferToBuyList список частей предложения продавца.
     * PartOfferToBuy содержит внешний ключ от SellerOffer, и является владеющей стороной,
     * что отражено в mappedBy = "sellerOffer", что означает, что в PartOfferToBuy есть поле SellerOffer.
     * SellerOffer есть родительская сущность и управляет жизненным циклом PartOfferToBuy,
     * то есть все изменения в SellerOffer каскадом переходят в PartOfferToBuy (cascade = CascadeType.ALL).
     * orphanRemoval = true означает, что при методе partOfferToBuyList.remove(partOfferToBuy),
     * автоматом удаляется partOfferToBuy из БД. Это заслуга Hibernate.
     * List<PartOfferToBuy> необходим в BuyerOrderServiceImpl, когда применяется метод limit().
     */
    @Builder.Default
    @OneToMany(mappedBy = "sellerOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartOfferToBuy> partOfferToBuyList = new ArrayList<>();

    /**
     * метод добавления заказанной части оффера к множеству всех заказанных частей данного оффера
     *
     * @param partOfferToBuy часть оффера продавца, заказанного покупателем
     */
    public void addPartOfferToBuy(@NonNull PartOfferToBuy partOfferToBuy) {
        partOfferToBuyList.add(partOfferToBuy);
        partOfferToBuy.setSellerOffer(this);
    }

    /**
     * метод удаления заказанной части оффера из множества всех заказанных частей данного оффера
     *
     * @param partOfferToBuy часть оффера продавца, заказанного покупателем
     */
    public void removePartOfferToBuySet(@NonNull PartOfferToBuy partOfferToBuy) {
        partOfferToBuyList.remove(partOfferToBuy);
        partOfferToBuy.setSellerOffer(null);
    }

    /**
     * количество единиц товара в оффере или количество частей, которые ещё не заказаны.
     *
     * @return количество незаказанных (частей) = единиц товара
     */
    public int getActualUnitCount() {
       return (int) partOfferToBuyList.stream()
                .filter(part -> part.getStatus().equals(PartOfferToBuyStatus.NOT_RESERVED))
                .count();
    }
}
