package org.gafiev.peertopeerbazaar.common;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.ProductRepository;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Тестовые данные (константы).
 */
@Slf4j
public class TestDataSellerOffer {
    public static final Faker faker = new Faker();

    public static final long EXIST_SELLER_OFFER_ID1 = 12L;
    public static final long EXIST_SELLER_OFFER_ID2 = 13L;
    public static final long EXIST_SELLER_OFFER_ID3 = 14L;


    private static @NotNull BigDecimal getRandomBigDecimal() {
        return new BigDecimal(String.format(Locale.US, "%.2f", ThreadLocalRandom.current().nextDouble(0.0, 1000.0)));
    }

    public static SellerOffer getSellerOfferSample(){
        OfferStatus status = Arrays.stream(OfferStatus.values()).skip(ThreadLocalRandom.current()
                .nextInt(OfferStatus.values().length)).findFirst().orElseThrow();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationDT;
        if (status == OfferStatus.PRESALE) {
            // Для PRESALE, creationDT должен быть в будущем
            creationDT = now.plusDays(ThreadLocalRandom.current().nextInt(1, 5))  // В будущем
                    .withHour(ThreadLocalRandom.current().nextInt(0, 23))
                    .withMinute(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);
        } else {  // Для OPENED или других
            creationDT = now.minusDays(ThreadLocalRandom.current().nextInt(0, 5))
                    .minusHours(ThreadLocalRandom.current().nextInt(0, 23))
                    .minusMinutes(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);
        }
        LocalDateTime finishDT = creationDT.plusDays(ThreadLocalRandom.current().nextInt(6, 10))
                .withHour(ThreadLocalRandom.current().nextInt(1, 23))
                .withMinute(ThreadLocalRandom.current().nextInt(1, 59))
                .withSecond(0);

        return  SellerOffer.builder()
                .offerStatus(status)
                .comment(faker.lorem().sentence())
                .creationDateTime(creationDT)
                .finishDateTime(finishDT)
                .createdAt(Instant.now().minusSeconds(3600))
                .build();
    }

    public static SellerOfferCreateRequest updateSellerOfferRequest(ProductRepository productRepository,AddressRepository addressRepository){
        OfferStatus offerStatus = Arrays.stream(OfferStatus.values()).skip(ThreadLocalRandom.current()
                .nextInt(0,3)).findFirst().orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationDT;
        if (offerStatus == OfferStatus.PRESALE) {
            // Для PRESALE, creationDT должен быть в будущем
            creationDT = now.plusDays(ThreadLocalRandom.current().nextInt(1, 5))  // В будущем
                    .withHour(ThreadLocalRandom.current().nextInt(0, 23))
                    .withMinute(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);
        } else {  // Для OPENED или других
            creationDT = now.minusDays(ThreadLocalRandom.current().nextInt(0, 5))
                    .minusHours(ThreadLocalRandom.current().nextInt(0, 23))
                    .minusMinutes(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);
        }

        LocalDateTime finishDT = creationDT.plusDays(ThreadLocalRandom.current().nextInt(6, 10))
                .withHour(ThreadLocalRandom.current().nextInt(1, 23))
                .withMinute(ThreadLocalRandom.current().nextInt(1, 59))
                .withSecond(0);

        Product product = TestDataProduct.getProductSample();
        product = productRepository.save(product);
        Address address = TestDataAddress.getAddressSample();
        address = addressRepository.save(address);

        return new SellerOfferCreateRequest(
                ThreadLocalRandom.current().nextInt(0,11)
                ,offerStatus,
                faker.lorem().sentence(),
                creationDT,
                finishDT,
                product.getId(),
                address.getId(),
                Instant.now().minusSeconds(3600));
    }

    public static SellerOfferFilterRequest getSellerOfferFilterRequest(){
        return SellerOfferFilterRequest.builder()
                .ids(Set.of(12L,13L,14L,15L,16L))
                .offerStatus(OfferStatus.valueOf("OPENED"))
                .creationDateTimeAfter(LocalDateTime.of(2025,10,10,23,37))
                .creationDateTimeBefore(LocalDateTime.of(2025,10,15,9,30))
                .finishDateTimeAfter(LocalDateTime.of(2025,10,14,22,30))
                .finishDateTimeBefore(LocalDateTime.of(2025,10,20,22,30))
                .productIds(Set.of(11L,12L,13L))
                .addressIds(Set.of(11L,12L,13L,14L,15L))
                .userIds(Set.of(101L,102L,103L,104L))
                .build();
    }


}


