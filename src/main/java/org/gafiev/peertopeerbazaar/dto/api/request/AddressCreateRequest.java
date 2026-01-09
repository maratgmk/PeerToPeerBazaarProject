package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO AddressCreateRequest is data for address creation.
 *
 * @param town Town name.
 * @param street Street name.
 * @param buildingNumber Number of building.
 * @param postCode  Post code of address.
 * @param latitude  Latitude of address point.
 * @param longitude Longitude of address point.
 * @param altitude  Altitude of address point.
 * @param accuracy   The spatial accuracy of the coordinates (horizontal and vertical, in meters).
// * @param createdAt Time when the address creation request was created.
 */
@Schema(description = "Data of address creation")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record AddressCreateRequest(

        @Schema(description = "Name of town", example = "Kazan")
        @Nonnull @NotNull @Size(min = 1, max = 149, message = "Town must be between 1 and 149 characters long.")
        String town,

        @Schema(description = "Name of street", example = "Esperanto")
        @Nonnull @NotNull @Size(min = 1, max = 149, message = "Street must be between 1 and 149 characters long.")
        String street,

        @Schema(description = "Number of house", example = "53")
        @Nonnull @NotNull @Positive(message = "Building number must be a positive number.")
        Integer buildingNumber,

        @Schema(description = "Number of post code", example = "420089")
        @Nonnull @NotNull @Positive(message = "Post code must be a positive number.")
        Integer postCode,

        @Schema(name = "Latitude of address point", example = "56.221099")
        @NotNull
        @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 2 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")
        Double latitude,

        @Schema(name = "Longitude of address point", example = "49.050736")
        @NotNull
        @Positive(message = "Longitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Longitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")
        Double longitude,

        @Schema(name = "Altitude of address point", example = "258.73")
        @NotNull
        @Positive(message = "Altitude must be a positive number.")
        @Digits(integer = 4, fraction = 2, message = "Altitude must have at most 4 digits before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double altitude,

        @Schema(description = "The spatial accuracy of the location (including altitude), in meters.", example = "5.36")
        @NotNull
        @PositiveOrZero(message = "Accuracy must be a not negative number.")
        @Digits(integer = 1, fraction = 2, message = "Accuracy must have at most 1 digit before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double accuracy

//        @Schema(description = "Time when the address creation request was created.", example = "2025-10-15T14:30:45.123456Z")
//        @Nonnull @PastOrPresent
//        @JsonFormat(shape = JsonFormat.Shape.STRING)
//        Instant createdAt
) {
}
