package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record PartOfferToBuyFilterRequest(
        Set<Long> ids,
        Integer unitCountLow,
        Integer unitCountHigh,
        Set<Long> sellerOfferIds,
        Set<Long> buyerOrderIds,
        Set<Long> basketIds )  {
}
