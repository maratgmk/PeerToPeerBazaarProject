package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;
import org.gafiev.peertopeerbazaar.entity.product.PortionUnit;

import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
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
        @Digits(integer = 6, fraction = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        BigDecimal weight,

        @Nonnull @Positive(message = "Volume can not be negative")
        @Digits(integer = 6, fraction = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        BigDecimal volume,

        @Nonnull @PositiveOrZero(message = "Price can not be negative")
        @Digits(integer = 12, fraction = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        BigDecimal price,

        @Nonnull @Size(min = 1, max = 249)
        String imageURI,

        @Nonnull @Size(min = 1, max = 249)
        String qrCode,

        @Nonnull @Positive(message = "Id of user must be positive")
        Long userId,

        @Nonnull @PastOrPresent
        Instant createdAt) {
}
