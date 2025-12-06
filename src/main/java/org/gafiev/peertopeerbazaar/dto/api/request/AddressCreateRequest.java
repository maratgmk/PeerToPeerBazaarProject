package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;

/**
 * DTO данные адреса для проверки его доступности для дрона.
 * приложение PTPB делает запрос во внешний сервис о проверке доступности обслуживания адреса.
 *
 * @param town
 * @param street
 * @param numberBuilding
 * @param zipCode
 * @param latitude
 * @param longitude
 * @param attitude
 * @param accuracy
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record AddressCreateRequest(
        @Nonnull @NotNull @Size(min = 1, max = 149, message = "Town must be between 1 and 149 characters long.")
        String town,

        @Nonnull @NotNull @Size(min = 1, max = 149, message = "Street must be between 1 and 149 characters long.")
        String street,

        @Nonnull @NotNull @Positive(message = "Number building must be a positive number.")
        Integer numberBuilding,

        @Nonnull @NotNull @Positive(message = "Zip code must be a positive number.")
        Integer zipCode,

        @NotNull
        @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 2 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")  // Форматирует как строку с 6 знаками после запятой при JSON-обмене
        Double latitude,

        @NotNull
        @Positive(message = "Longitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Longitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")
        Double longitude,

        @NotNull
        @Positive(message = "Attitude must be a positive number.")
        @Digits(integer = 4, fraction = 2, message = "Attitude must have at most 4 digits before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double attitude,

        @NotNull
        @PositiveOrZero(message = "Accuracy must be a not negative number.")
        @Digits(integer = 1, fraction = 2, message = "Accuracy must have at most 1 digit before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double accuracy,

        @Nonnull @PastOrPresent
        Instant createdAt
) {
}
