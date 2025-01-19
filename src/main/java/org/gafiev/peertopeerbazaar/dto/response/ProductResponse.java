package org.gafiev.peertopeerbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;
import org.gafiev.peertopeerbazaar.entity.product.PortionUnit;

import java.math.BigDecimal;
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
        Double weight,
        Double volume,
        BigDecimal price,
        String imageURI,
        String qrCode,
        Long userId,
        Set<SellerOfferResponse> sellerOfferResponseSet
) {
}
