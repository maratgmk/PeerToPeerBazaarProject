package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.gafiev.peertopeerbazaar.entity.product.Category;
import org.gafiev.peertopeerbazaar.entity.product.PortionUnit;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductCreateRequest(
        @Nonnull @Size(min = 1, max = 49)
        String name,

        @Nonnull @Size(min = 1, max = 249)
        String description,

        @Nonnull
        Category category,

        @Nonnull
        PortionUnit portionUnit,

        @Nonnull @Positive(message = "Weight can not be negative")
        Double weight,

        @Nonnull @Positive(message = "Volume can not be negative")
        Double volume,

        @Nonnull @PositiveOrZero(message = "Price can not be negative")
        @Digits(integer = 5, fraction = 2)
        BigDecimal price,

        @Nonnull @Size(min = 1, max = 249)
        String imageURI,

        @Nonnull @Size(min = 1, max = 249)
        String qrCode,

        @Nonnull @Positive(message = "Id of user must be positive")
        Long userId) {
}
