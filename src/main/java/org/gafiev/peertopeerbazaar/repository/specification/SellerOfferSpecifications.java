package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SellerOfferSpecifications {
    public static Specification<SellerOffer> filterByParams(@Nullable SellerOfferFilterRequest request) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request == null) return criteriaBuilder.conjunction();

            if (request.ids() != null && !request.ids().isEmpty()) {
                predicates.add(root.get("id").in(request.ids()));
            }

            if (request.productIds() != null && !request.productIds().isEmpty()) {
                predicates.add(root.get("id").in(request.productIds()));
            }

            if (request.addressIds() != null && !request.addressIds().isEmpty()) {
                predicates.add(root.get("id").in(request.addressIds()));
            }

            if (request.userIds() != null && !request.userIds().isEmpty()) {
                predicates.add(root.get("id").in(request.userIds()));
            }

            if (request.offerStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("offerStatus"), request.offerStatus().name()));
            }

//            if (request.unitCountHigh() != null) {
//                predicates.add(criteriaBuilder.le(root.get("unit_count"), request.unitCountHigh()));
//            }
//
//            if (request.unitCountLow() != null) {
//                predicates.add(criteriaBuilder.ge(root.get("unit_count"), request.unitCountLow()));
//            }

            if (request.creationDateTimeAfter() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("creationDateTime"), request.creationDateTimeAfter()));
            }

            if (request.creationDateTimeBefore() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("creationDateTime"), request.creationDateTimeBefore()));
            }

            if (request.creationDateTimeAfter() != null && request.creationDateTimeBefore() != null) {
                if (request.creationDateTimeBefore().isAfter(request.creationDateTimeAfter())) {
                    return criteriaBuilder.conjunction(); // Возвращаем пустой предикат
                } else {
                    predicates.add(criteriaBuilder.between(
                            root.get("creation_date_time"),
                            request.creationDateTimeBefore(),
                            request.creationDateTimeAfter()
                    ));
                }
            }

            if (request.finishDateTimeAfter() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("finishDateTime"), request.finishDateTimeAfter()));
            }

            if (request.creationDateTimeBefore() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("finishDateTime"), request.creationDateTimeBefore()));
            }


            if (request.finishDateTimeAfter() != null && request.finishDateTimeBefore() != null) {
                if (request.finishDateTimeBefore().isAfter(request.finishDateTimeAfter())) {
                    return criteriaBuilder.conjunction(); // Возвращаем пустой предикат
                } else {
                    predicates.add(criteriaBuilder.between(
                            root.get("finishDateTime"),
                            request.finishDateTimeBefore(),
                            request.finishDateTimeAfter()
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
