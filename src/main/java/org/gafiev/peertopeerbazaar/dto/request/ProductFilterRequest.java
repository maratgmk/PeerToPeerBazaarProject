package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;

import java.math.BigDecimal;
import java.util.Set;

/**
 * фильтр для поиска множества продуктов по заданным параметрам
 * @param ids множество идентификаторов продуктов
 * @param name префикс названия продукта
 * @param descriptionKeyWords набор ключевых слов
 * @param category категория продукта
 * @param priceHigher верхняя граница цены продукта
 * @param priceLower нижняя граница цены продукта
 * @param qrCode ку эр код продукта
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record ProductFilterRequest(
        @Size(min = 1) Set<Long> ids,
        @Size(min = 1) String name,
        @Size(min = 1) Set<@Size(min = 1) String> descriptionKeyWords,
        Category category,
        @Positive BigDecimal priceHigher,
        @Positive BigDecimal priceLower,
        @Size(min = 1) String qrCode
) {
}
