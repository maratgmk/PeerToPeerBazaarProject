package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.SellerOfferFilterRequest;
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
                predicates.add(root.get("product").in(request.productIds()));
            }

            if (request.addressIds() != null && !request.addressIds().isEmpty()) {
                predicates.add(root.get("id").in(request.addressIds()));
            }

            if (request.userIds() != null && !request.userIds().isEmpty()) {
                predicates.add(root.get("id").in(request.userIds()));
            }

            if (request.offerStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("offer_status"), request.offerStatus().name()));
            }

            if (request.unitCountHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("unit_count"), request.unitCountHigh()));
            }

            if (request.unitCountLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("unit_count"), request.unitCountLow()));
            }

            if (request.creationDateTimeEarlier() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("creation_date_time"), request.creationDateTimeEarlier()));
            }

            if (request.creationDateTimeLater() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("creation_date_time"), request.creationDateTimeLater()));
            }

            if (request.finishDateTimeEarlier() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("finish_date_time"), request.finishDateTimeEarlier()));
            }

            if (request.creationDateTimeLater() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("finish_date_time"), request.creationDateTimeLater()));
            }




            if (request.creationDateTimeEarlier() != null && request.creationDateTimeLater() != null) {
                if (request.creationDateTimeLater().isBefore(request.creationDateTimeEarlier())) {
                    return criteriaBuilder.conjunction(); // Возвращаем пустой предикат
                } else {
                    predicates.add(criteriaBuilder.between(
                            root.get("creation_date_time"),
                            request.creationDateTimeLater(),
                            request.creationDateTimeEarlier()
                    ));
                }
            } else {
                if (request.creationDateTimeEarlier() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("creation_date_time"), request.creationDateTimeEarlier()));
                }

                if (request.creationDateTimeLater() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("creation_date_time"), request.creationDateTimeLater()));
                }
            }

            if (request.finishDateTimeEarlier() != null && request.finishDateTimeLater() != null) {
                if (request.finishDateTimeLater().isBefore(request.finishDateTimeEarlier())) {
                    return criteriaBuilder.conjunction(); // Возвращаем пустой предикат
                } else {
                    predicates.add(criteriaBuilder.between(
                            root.get("finish_date_time"),
                            request.finishDateTimeLater(),
                            request.finishDateTimeEarlier()
                    ));
                }
            } else {
                if (request.finishDateTimeEarlier() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("finish_date_time"), request.finishDateTimeEarlier()));
                }

                if (request.finishDateTimeLater() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("finish_date_time"), request.finishDateTimeLater()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
//TODO проверить и оставить один метод проверки по датам