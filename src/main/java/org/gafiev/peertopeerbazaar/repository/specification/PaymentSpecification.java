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
 * Utility class for creating JPA Specifications to filter Payment entities dynamically
 * based on criteria provided in a PaymentFilterRequest DTO.
 * This utilizes the Spring Data JPA Specification interface in conjunction with JpaSpecificationExecutor
 * to build dynamic query predicates.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentSpecification {
    /**
     * Creates a JPA Specification to filter Payment entities based on the provided criteria.
     * The specification dynamically builds a list of predicates which are combined using AND logic.
     *
     * Criteria applied include:
     * - `ids`: Matches payments whose primary ID is within the provided set.
     * - `paymentStatus`: Matches payments by an exact status match.
     * - `paymentMode`: Matches payments by an exact mode match.
     * - Amount Ranges: Filters payments based on minimum/maximum amount transaction ranges.
     * - CompletionDateTime Ranges: Filters payments based on start/end of the completion date-time ranges.
     *
     * If the filterRequest is null or empty, an "always true" predicate (conjunction) is returned, applying no filters.

     * @param filterRequest PaymentFilterRequest DTO containing filter criteria (can be null).
     * @return Specification<Payment> for use with JpaSpecificationExecutor.
     */
    public static Specification<Payment> filterByParams(@Nullable PaymentFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            // Filter by specific payment IDs if provided
            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            // Filter by maximum amount (amount <= amountHigh)
            if(filterRequest.amountHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("amount"), filterRequest.amountHigh()));
            }

            // Filter by minimum amount (amount >= amountLow)
            if(filterRequest.amountLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("amount"), filterRequest.amountLow()));
            }

            // Filter by exact payment mode match
            if(filterRequest.paymentMode() != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentMode"), filterRequest.paymentMode().name()));
            }

            // Filter by exact payment status match
            if(filterRequest.paymentStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentStatus"), filterRequest.paymentStatus().name()));
            }

            // Filter by completion date-time range (between after and before, inclusive)
            if(filterRequest.completionDateTimeBefore() != null && filterRequest.completionDateTimeAfter() != null) {
                predicates.add(criteriaBuilder.between(root.get("completionDateTime"), filterRequest.completionDateTimeAfter(), filterRequest.completionDateTimeBefore()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
