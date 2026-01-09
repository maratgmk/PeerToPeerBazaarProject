package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.UserResponse;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting User entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class UserMapper {
    private final ProductMapper productMapper;
    private final BuyerOrderMapper buyerOrderMapper;
    private final SellerOfferMapper sellerOfferMapper;

    /**
     * Converts User entity to UserResponse DTO.
     *
     * @param user User entity.
     * @return UserResponse DTO.
     */
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(user.getRoles())
                .ratingSeller(user.getRatingSeller())
                .ratingBuyer(user.getRatingBuyer())
                .sellerOfferResponseSet(sellerOfferMapper.toSellerOfferResponseSet(user.getSellerOfferSet()))
                .buyerOrderResponseSet(buyerOrderMapper.toBuyerOrderResponseSet(user.getBuyerOrderSet()))
                .productSet(productMapper.toProductResponseSet(user.getProductSet()))
                .build();
    }

    /**
     * Converts Set of User entities to Set of UserResponse DTOs.
     *
     * @param users Set of User entities.
     * @return Set of UserResponse DTOs.
     */
    public Set<UserResponse> toUserResponseSet(Set<User> users) {
        return users == null ? null : users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toSet());
    }
}
