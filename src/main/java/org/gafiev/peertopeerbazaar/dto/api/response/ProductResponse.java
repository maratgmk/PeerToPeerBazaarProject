package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;
import org.gafiev.peertopeerbazaar.entity.product.PortionUnit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        Category category,
        PortionUnit portionUnit,
        Integer portionUnitCount,
        BigDecimal weight,
        BigDecimal volume,
        BigDecimal price,
        String imageURI,
        String qrCode,
        Long userId,
        Instant createdAt,
        Set<SellerOfferResponse> sellerOfferResponseSet
) {
}
