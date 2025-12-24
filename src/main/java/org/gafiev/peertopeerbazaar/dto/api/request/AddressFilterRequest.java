package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

/**
 * Searching necessary (interested) addresses from database according the specific filter.
 *
 * @param ids are set of interested ids.
 * @param town  of address
 * @param street  of address
 * @param numbers are set of building numbers
 * @param longitudeRight is longitude limit on the right (east)
 * @param longitudeLeft  is longitude limit on the left (west)
 * @param latitudeNorth is latitude limit on the north
 * @param latitudeSouth is latitude limit on the south
 * @param altitudeHigh is upper level height restriction
 * @param altitudeLow  is low level height restriction
 */
@Schema(description = "Data for the specific filter to get all interested addresses")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record AddressFilterRequest(
        @Schema(description = "Set of Ids that should be looked for.", example = "[27,29,53]")
        @NotNull @Nonnull @Size(min = 1) Set<Long> ids,

        @Schema(description = "Name of town.", example = "Kirov")
        @NotNull @Nonnull @Size(min = 1) String town,

        @Schema(description = "Name of street.", example = "Maklina")
        @NotNull @Nonnull @Size(min = 1) String street,

        @Schema(description = "Set of building numbers.", example = "[12,17,31]")
        @NotNull @Nonnull @Size(min = 1) Set<Integer> numbers,

        @Schema(description = "Longitude limit on the right.", example = "51.070336")
        @NotNull @Nonnull @Positive(message = "Longitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")
        Double longitudeRight,

        @Schema(description = "Longitude limit on the right.", example = "49.050736")
        @NotNull @Nonnull @Positive(message = "Longitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")
        Double longitudeLeft,

        @Schema(description = "Latitude limit on the north.", example = "56.221099")
        @NotNull @Nonnull @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")  // Форматирует как строку с 6 знаками после запятой при JSON-обмене
        Double latitudeNorth,

        @Schema(description = "Latitude limit on the south.", example = "53.223579")
        @NotNull @Nonnull @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")
        Double latitudeSouth,

        @Schema(description = "Upper level height restriction.", example = "102.28")
        @NotNull @Nonnull @DecimalMin(value = "-500.00", message = "Altitude must be at least -500.00.")
        @Digits(integer = 4, fraction = 2, message = "Altitude must have at most 4 digits before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double altitudeHigh,

        @Schema(description = "Lower level height restriction.", example = "37.59")
        @NotNull @Nonnull @DecimalMin(value = "-500.00", message = "Altitude must be at least -500.00.")
        @Digits(integer = 4, fraction = 2, message = "Altitude must have at most 4 digits before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double altitudeLow) {
}
