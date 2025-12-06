package org.gafiev.peertopeerbazaar.common;

import com.github.javafaker.Faker;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataUser {
    public static final Faker faker = new Faker();
    public static final long EXISTING_ADMIN1_ID = 104L;
    public static final long USER1_ID = 101L;
    public static final long BUYER2_ID = 102L;

    public static User getUserPrototype(boolean isSeller) {
        User prototype = new User();
        prototype.setFirstName(faker.name().firstName());
        prototype.setLastName(faker.name().lastName());
        prototype.setPhone(faker.phoneNumber().cellPhone());
        prototype.setEmail(faker.internet().emailAddress());
        prototype.setPassword(faker.internet().password());
        prototype.setRatingBuyer(ThreadLocalRandom.current().nextInt(0, 11));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.BUYER);

        if (isSeller) {
            roles.add(Role.SELLER);
            prototype.setRatingSeller(0);
        }
        prototype.setRoles(roles);

        return prototype;
    }

    public static UserFilterRequest getUserFilter() {
        return UserFilterRequest.builder()
                .ids(Set.of(101L, 102L, 103L))
                .roles(Set.of(Role.USER, Role.BUYER))
                .ratingBuyerHigh(2)
                .ratingBuyerLow(3)
                .ratingSellerHigh(2)
                .ratingSellerLow(4)
                .build();
    }
}
