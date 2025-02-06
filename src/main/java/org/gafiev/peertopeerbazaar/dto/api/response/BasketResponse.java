package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record BasketResponse(Long id, Set<PartOfferToBuyResponse> parts) {
}
