package org.gafiev.peertopeerbazaar.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestData {
    public static final Address ADDRESS1 = Address.builder()
           // .id(1L)
            .town("Omsk")
            .street("Lenina")
            .numberBuilding(12)
            .zipCode(123768)
            .longitude(54.9914)
            .latitude(73.3645)
            .attitude(121.15)
            .accuracy(30.15)
            .build();

    public static final Address ADDRESS2 = Address.builder()
          //  .id(2L)
            .town("Tomsk")
            .street("Lenina")
            .numberBuilding(25)
            .zipCode(223768)
            .longitude(56.4975)
            .latitude(84.9742)
            .attitude(105.15)
            .accuracy(30.15)
            .build();

    public static final SellerOffer SELLER_OFFER1 = SellerOffer.builder()
         //   .id(117L)
            .unitCount(11)
            .offerStatus(OfferStatus.PRESALE)
            .comment("Свежая лесная земляника")
            .creationDateTime(LocalDateTime.now())
            .finishDateTime(LocalDateTime.now().plusHours(8))
            .build();

    public static final SellerOffer SELLER_OFFER2 = SellerOffer.builder()
//            .id(111L)
            .unitCount(8)
            .offerStatus(OfferStatus.CLOSED)
            .comment("Today chicken eggs")
            .creationDateTime(LocalDateTime.of(2024,10,23,9,0))
            .finishDateTime(LocalDateTime.of(2024,10,23,11,47))
            .build();

    public static final Delivery DELIVERY1 = Delivery.builder()
//            .id(25L)
            .deliveryStatus(DeliveryStatus.CREATED)
            .timeSlot(new TimeSlot(LocalDateTime.of(2025,3,15,11,30,00),
                    LocalDateTime.of(2025,3,15,11,30,00).plusMinutes(30)))
            .build();

    public static final Delivery DELIVERY2 = Delivery.builder()
//            .id(11L)
            .deliveryStatus(DeliveryStatus.DELIVERED)
            .timeSlot(new TimeSlot(LocalDateTime.of(2025,3,15,12,00,00),
                    LocalDateTime.of(2025,3,15,12,00,00).plusMinutes(30)))
            .build();
}
