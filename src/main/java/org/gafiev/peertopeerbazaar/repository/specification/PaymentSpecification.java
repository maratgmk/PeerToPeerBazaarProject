package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentSpecification {
    public static Specification<Payment> filterByParams(@Nullable PaymentFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            if(filterRequest.amountHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("amount"), filterRequest.amountHigh()));
            }

            if(filterRequest.amountLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("amount"), filterRequest.amountLow()));
            }

            if(filterRequest.paymentMode() != null) {
                predicates.add(criteriaBuilder.equal(root.get("payment_mode"), filterRequest.paymentMode().name()));
            }

            if(filterRequest.paymentStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("payment_status"), filterRequest.paymentStatus().name()));
            }

            if(filterRequest.completionDateTimeEarlier() != null && filterRequest.completionDateTimeLater() != null) {
                predicates.add(criteriaBuilder.between(root.get("completion_date_time"), filterRequest.completionDateTimeLater(), filterRequest.completionDateTimeEarlier()));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
