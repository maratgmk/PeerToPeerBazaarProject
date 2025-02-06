package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliverySpecification {
    public static Specification<Delivery> filterByParams(@Nullable DeliveryFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filterRequest == null) return criteriaBuilder.conjunction();

            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            if (filterRequest.deliveryStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("delivery_status"), filterRequest.deliveryStatus().name()));
            }

            if (filterRequest.expectedDateTimeEarlier() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expected_date_time"), filterRequest.expectedDateTimeEarlier()));
            }

            if (filterRequest.expectedDateTimeLater() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expected_date_time"), filterRequest.expectedDateTimeLater()));
            }

            if (filterRequest.expectedDateTimeEarlier() != null && filterRequest.expectedDateTimeLater() != null) {
                if (filterRequest.expectedDateTimeLater().isAfter(filterRequest.expectedDateTimeEarlier())) {
                    return criteriaBuilder.conjunction(); // Возвращаем пустой предикат
                } else {
                    predicates.add(criteriaBuilder.between(
                            root.get("expected_date_time"),
                            filterRequest.expectedDateTimeLater(),
                            filterRequest.expectedDateTimeLater()
                    ));
                }
            }

            if (filterRequest.buyerOrderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterRequest.buyerOrderId()));
            }

            if (filterRequest.droneId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterRequest.droneId()));
            }

            if (filterRequest.fromAddressId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterRequest.fromAddressId()));
            }

            if (filterRequest.toAddressId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterRequest.toAddressId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };

    }
}
