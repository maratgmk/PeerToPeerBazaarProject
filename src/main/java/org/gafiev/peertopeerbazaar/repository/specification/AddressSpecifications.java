package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressFilterRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressSpecifications {
    public static Specification<Address> filterByParams(@Nullable AddressFilterRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request == null) return criteriaBuilder.conjunction();

            if (request.ids() != null && !request.ids().isEmpty()) {
                predicates.add(root.get("id").in(request.ids()));
            }

            if (request.longitudeRight() != null) {
                predicates.add(criteriaBuilder.le(root.get("longitude"), request.longitudeRight()));
            }

            if (request.longitudeLeft() != null) {
                predicates.add(criteriaBuilder.ge(root.get("longitude"), request.longitudeLeft()));
            }

            if (request.latitudeNorth() != null) {
                predicates.add(criteriaBuilder.le(root.get("latitude"), request.latitudeNorth()));
            }

            if (request.latitudeSouth() != null) {
                predicates.add(criteriaBuilder.ge(root.get("latitude"), request.latitudeSouth()));
            }

            if (request.attitudeHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("attitude"), request.attitudeHigh()));
            }

            if (request.attitudeLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("attitude"), request.attitudeLow()));
            }

            if (request.town() != null && !request.town().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("town"),  request.town()));
            }
            if (request.street() != null && !request.street().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("street"), request.street()));
            }
            if (request.numberBuilding() != null) {
                predicates.add(criteriaBuilder.equal(root.get("number_building"), request.numberBuilding()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
