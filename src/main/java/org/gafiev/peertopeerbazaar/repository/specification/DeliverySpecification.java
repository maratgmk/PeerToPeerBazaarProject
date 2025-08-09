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

            if (filterRequest.timeSlotEarlier() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timeSlot").get("start"), filterRequest.timeSlotEarlier().getStart()));
            }

            if (filterRequest.timeSlotTimeLater() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timeSlot").get("end"), filterRequest.timeSlotTimeLater().getEnd()));
            }

            if (filterRequest.timeSlotEarlier() != null && filterRequest.timeSlotTimeLater() != null) {
                // Проверяем, что временные метки корректны
                if (filterRequest.timeSlotTimeLater().getStart().isAfter(filterRequest.timeSlotEarlier().getEnd())) {
                    return criteriaBuilder.conjunction(); // Возвращаем пустой предикат
                } else {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(root.get("timeSlot").get("end"), filterRequest.timeSlotTimeLater().getEnd()),
                            criteriaBuilder.greaterThanOrEqualTo(root.get("timeSlot").get("start"), filterRequest.timeSlotEarlier().getStart())
                    ));
                }
            }

            if (filterRequest.buyerOrderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("buyerOrder").get("id"), filterRequest.buyerOrderId()));
            }

            if (filterRequest.droneId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("drone").get("id"), filterRequest.droneId()));
            }

            if (filterRequest.fromAddressId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("fromAddress").get("id"), filterRequest.fromAddressId()));
            }

            if (filterRequest.toAddressId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("toAddress").get("id"), filterRequest.toAddressId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };

    }
}
