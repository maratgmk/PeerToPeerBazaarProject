package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.user.Role;

import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Role role,
        Integer ratingSeller,
        Integer ratingBuyer,
        Set<ProductResponse> productSet,
        Set<SellerOfferResponse> sellerOfferResponseSet,
        Set<BuyerOrderResponse> buyerOrderResponseSet,
        Set<PaymentAccountResponse> paymentAccountSet) {
}
