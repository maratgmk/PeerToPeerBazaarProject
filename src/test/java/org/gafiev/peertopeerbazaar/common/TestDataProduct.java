package org.gafiev.peertopeerbazaar.common;

import com.github.javafaker.Faker;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.entity.product.Category;
import org.gafiev.peertopeerbazaar.entity.product.PortionUnit;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataProduct {
    public static final Faker faker = new Faker();

    public static final long EXIST_PRODUCT_ID = 11L;

    private static @NotNull BigDecimal getRandomBigDecimal() {
        return new BigDecimal(String.format(Locale.US, "%.2f", ThreadLocalRandom.current().nextDouble(0.0, 1000.0)));
    }



    public static ProductFilterRequest getProductFilter(){
        return ProductFilterRequest.builder()
                .ids(Set.of(11L,12L,13L))
                .category(Category.FRAGILE)
                .priceLower(BigDecimal.valueOf(750.49))
                .build();
    }

    public static Product getProductSample(){
        Product product = new Product();
        product.setName(faker.commerce().productName());
        product.setPortionUnit(PortionUnit.values()[ThreadLocalRandom.current().nextInt(PortionUnit.values().length)]);
        product.setDescription(faker.lorem().sentence(10));
        product.setVolumeLtr(getRandomBigDecimal());
        product.setWeightKg(getRandomBigDecimal());
        product.setAuthor(TestDataUser.getUserPrototype(true));
        product.setCategory(Category.values()[ThreadLocalRandom.current().nextInt(Category.values().length)]);
        product.setPrice(getRandomBigDecimal());
        product.setImageURI(faker.internet().image());
        product.setQrCode(faker.internet().url());
        product.setCreatedAt(Instant.now());
        return product;
    }
}
