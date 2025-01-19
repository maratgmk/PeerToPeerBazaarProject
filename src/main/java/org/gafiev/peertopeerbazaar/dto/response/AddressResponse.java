package org.gafiev.peertopeerbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;

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
        Set<SellerOfferResponse> sellerOfferResponseSet,
        Set<DeliveryResponse> deliveryResponseSet) {
}
