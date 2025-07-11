package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO данные адреса для проверки его доступности для дрона.
 * приложение PTPB делает запрос во внешний сервис о проверке доступности обслуживания адреса.
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
public record AddressCreateRequest(
        @Nonnull @NotNull @Size(min = 1, max = 149, message = "Town must be between 1 and 149 characters long.")
        String town,

        @Nonnull @NotNull @Size(min = 1, max = 149, message = "Street must be between 1 and 149 characters long.")
        String street,

        @Nonnull @NotNull @Positive(message = "Number building must be a positive number.")
        Integer numberBuilding,

        @Nonnull @NotNull @Positive(message = "Zip code must be a positive number.")
        Integer zipCode,

        @Nonnull @NotNull @Positive(message = "Latitude must be a positive number.")
        Double latitude,

        @Nonnull @NotNull @Positive(message = "Longitude must be a positive number.")
        Double longitude,

        @Nonnull @NotNull
        Double attitude,

        @Nonnull @NotNull @PositiveOrZero(message = "Accuracy must be a not negative number.")
        Double accuracy
) {
}
