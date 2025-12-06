package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {
    public static Specification<User> filterByParams(UserFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            // Фильтр по ролям: JOIN на коллекцию roles + in() (хотя бы одна роль из filter.roles)
            if (filterRequest.roles() != null && !filterRequest.roles().isEmpty()) {
                // JOIN на Set<Role> roles (ElementCollection генерирует виртуальный JOIN в SQL)
                Join<User, Role> rolesJoin = root.join("roles", JoinType.INNER);  // <-- Ключ: JOIN на коллекцию
                // in() на JOIN: проверяет, есть ли в ролях пользователя хотя бы одна из filter.roles
                predicates.add(rolesJoin.in(filterRequest.roles()));  // <-- Это Predicate (один аргумент для add)
            }

            if (filterRequest.ratingBuyerLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("ratingBuyer"), filterRequest.ratingBuyerLow()));
            }

            if (filterRequest.ratingBuyerHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("ratingBuyer"), filterRequest.ratingBuyerHigh()));
            }

            if (filterRequest.ratingSellerLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("ratingSeller"), filterRequest.ratingSellerLow()));
            }

            if (filterRequest.ratingSellerHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("ratingSeller"), filterRequest.ratingBuyerHigh()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };

    }

}
