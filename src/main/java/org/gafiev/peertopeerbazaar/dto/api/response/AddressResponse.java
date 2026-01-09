package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * DTO AddressResponse represents a complete Address structure.
 * Used as response body in Address management API endpoints.
 *
 * @param id             Unique Address identifier.
 * @param town           Town name.
 * @param street         Street name.
 * @param buildingNumber Building number.
 * @param postCode       Post code.
 * @param latitude       Latitude of address point.
 * @param longitude      Longitude of address point.
 * @param altitude       Altitude of address point.
 * @param accuracy       The spatial accuracy of the coordinates (horizontal and vertical, in meters).
// * @param createdAt      Timestamp of address response creation.
 */
@Schema(description = "Data transfer object (DTO) representing an address for API responses.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressResponse(
        @Schema(description = "Address ID.", example = "137")
        Long id,
        @Schema(description = "Name of town.", example = "Perm")
        String town,
        @Schema(description = "Name of street.", example = "Lesova")
        String street,
        @Schema(description = "Building number.", example = "71")
        Integer buildingNumber,
        @Schema(description = "Post code.", example = "832739")
        Integer postCode,
        @Schema(description = "Latitude of address point.", example = "57.941336")
        Double latitude,
        @Schema(description = "Longitude of address point.", example = "51.070336")
        Double longitude,
        @Schema(description = "Altitude of address point.", example = "151.36")
        Double altitude,
        @Schema(description = "The spatial accuracy of the location (including altitude), in meters.", example = "5.36")
        Double accuracy
//        @Schema(description = "Time of address response creation.", example = "2025-10-15T14:30:45.123456Z")
//        Instant createdAt
) {
}
