package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressResponse(
        Long id,
        String town,
        String street,
        Integer numberBuilding,
        Integer zipCode,
        Double latitude,
        Double longitude,
        Double attitude,
        Double accuracy,
        Instant createdAt

) {
}
