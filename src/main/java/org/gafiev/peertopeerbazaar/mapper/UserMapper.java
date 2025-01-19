package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.UserResponse;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserMapper {
    private final PaymentAccountMapper paymentAccountMapper;
    private final ProductMapper productMapper;
    private final BuyerOrderMapper buyerOrderMapper;
    private final SellerOfferMapper sellerOfferMapper;

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .ratingSeller(user.getRatingSeller())
                .ratingBuyer(user.getRatingBuyer())
                .sellerOfferResponseSet(sellerOfferMapper.toSellerOfferResponseSet(user.getSellerOfferSet()))
                .buyerOrderResponseSet(buyerOrderMapper.toBuyerOrderResponseSet(user.getBuyerOrderSet()))
                .productSet(productMapper.toProductResponseSet(user.getProductSet()))
                .paymentAccountSet(paymentAccountMapper.toPaymentAccountResponseSet(user.getPaymentAccountSet()))
                .build();


    }

    public Set<UserResponse> toUserResponseSet(Set<User> users){
        return users == null ? null : users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toSet());
    }
}
