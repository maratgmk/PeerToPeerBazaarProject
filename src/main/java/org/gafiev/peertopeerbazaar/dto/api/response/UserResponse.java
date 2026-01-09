package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.user.Role;

import java.util.Set;

/**
 * The UserResponse DTO represents a complete user structure.
 * Used as the response body in user management API endpoints.
 *
 * @param id                     Unique User identifier (ID).
 * @param firstName              User's first name.
 * @param lastName               User's last name.
 * @param email                  User's email.
 * @param phone                  User's phone.
 * @param roles                  Set of user roles.
 * @param ratingSeller           The seller's rating value.
 * @param ratingBuyer            The buyer's rating value.
 * @param productSet             Set of ProductResponse DTOs, which are made by the user.
 * @param sellerOfferResponseSet Set of SellerOfferResponse DTOs, which are offered by the seller.
 * @param buyerOrderResponseSet  Set of BuyerOrderResponse DTOs, which are created by the buyer.
 */
@Schema(description = "Data transfer object (DTO) representing an user for API responses.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserResponse(
        @Schema(description = "User ID.", example = "137")
        Long id,

        @Schema(description = "User's first name.", example = "Ivan.")
        String firstName,

        @Schema(description = "User's last name.", example = "Petrov.")
        String lastName,

        @Schema(description = "User's email.", example = "ivan.petrov@gmail.com")
        String email,

        @Schema(description = "User's phone.", example = "+7-968-543-12-34")
        String phone,

        @Schema(description = "Set of user roles.", example = "[\"USER\",\"BUYER\",\"SELLER\"]")
        Set<Role> roles,

        @Schema(description = "Number of seller's rating.", example = "29")
        Integer ratingSeller,

        @Schema(description = "Number of buyer's rating.", example = "27")
        Integer ratingBuyer,

        @Schema(description = "Set of ProductResponse DTOs, which are made by the user.")
        Set<ProductResponse> productSet,

        @Schema(description = "Set of SellerOfferResponse DTOs, which are offered by the seller.")
        Set<SellerOfferResponse> sellerOfferResponseSet,

        @Schema(description = "Set of BuyerOrderResponse DTOs, which are created by the buyer.")
        Set<BuyerOrderResponse> buyerOrderResponseSet) {
}
