package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressCreateRequest(
        @Nonnull @Size(min = 1, max = 149, message = "Town must be between 1 and 149 characters long.")
        String town,

        @Nonnull @Size(min = 1, max = 149, message = "Street must be between 1 and 149 characters long.")
        String street,

        @Nonnull @Positive(message = "Number building must be a positive number.")
        Integer numberBuilding,

        @Nonnull @Positive(message = "Zip code must be a positive number.")
        Integer zipCode,

        @Nonnull @Positive(message = "Latitude must be a positive number.")
        Double latitude,

        @Nonnull @Positive(message = "Longitude must be a positive number.")
        Double longitude,

        @Nonnull
        Double attitude,

        @Nonnull @PositiveOrZero(message = "Accuracy must be a not negative number.")
        Double accuracy
) {
}
