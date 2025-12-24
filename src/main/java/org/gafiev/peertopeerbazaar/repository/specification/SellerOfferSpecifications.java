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
 * Utility class for creating JPA Specifications to filter SellerOffer entities dynamically
 * based on criteria provided in a SellerOfferFilterRequest DTO.
 * This utilizes the Spring Data JPA Specification interface in conjunction with JpaSpecificationExecutor
 * to build dynamic query predicates.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SellerOfferSpecifications {
    /**
     * Creates a JPA Specification to filter SellerOffer entities based on the provided criteria.
     * The specification dynamically builds a list of predicates which are combined using AND logic.
     *
     * Criteria applied include:
     * - `ids`: Matches offers whose primary ID is within the provided set.
     * - `productIds`: Matches offers linked to products within the provided set of IDs.
     * - `addressIds`: Matches offers linked to addresses within the provided set of IDs.
     * - `userIds`: Matches offers created by sellers within the provided set of user IDs.
     * - `offerStatus`: Matches offers by an exact status match.
     * - Date Ranges: Filters offers based on creation and finish date/time ranges.
     *
     * If the filterRequest is null or empty, an "always true" predicate (conjunction) is returned, applying no filters.
     *
     * @param request SellerOfferFilterRequest DTO containing filter criteria (can be null).
     * @return Specification<SellerOffer> for use with JpaSpecificationExecutor.
     */
    public static Specification<SellerOffer> filterByParams(@Nullable SellerOfferFilterRequest request) {

        return (root, query, criteriaBuilder) -> {
            // Initialize a list to hold all predicates
            List<Predicate> predicates = new ArrayList<>();

            // If no filter request is provided, return a conjunction (no filters applied)
            if (request == null) return criteriaBuilder.conjunction();

            // Filters by IDs: Add predicate if IDs set is provided and not empty
            if (request.ids() != null && !request.ids().isEmpty()) {
                predicates.add(root.get("id").in(request.ids()));
            }

            // Filters by product IDs (Join to 'product' entity and filter by its 'id')
            if (request.productIds() != null && !request.productIds().isEmpty()) {
                predicates.add(root.get("id").in(request.productIds()));
            }

            // Filters by address IDs (Join to 'address' entity and filter by its 'id')
            if (request.addressIds() != null && !request.addressIds().isEmpty()) {
                predicates.add(root.get("id").in(request.addressIds()));
            }

            // Filters by user IDs (Join to 'seller' entity and filter by its 'id')
            if (request.userIds() != null && !request.userIds().isEmpty()) {
                predicates.add(root.get("id").in(request.userIds()));
            }

            // Filters seller offers by an exact match on the offer status.
            if (request.offerStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("offerStatus"), request.offerStatus().name()));
            }

            // --- Date Range Filters (Creation Date) ---
            if (request.creationDateTimeAfter() != null && request.creationDateTimeBefore() != null) {
                if (request.creationDateTimeBefore().isAfter(request.creationDateTimeAfter())) {
                    return criteriaBuilder.conjunction(); // Invalid range
                } else {
                    predicates.add(criteriaBuilder.between(
                            root.get("creationDateTime"),
                            request.creationDateTimeAfter(),  // min
                            request.creationDateTimeBefore()  // max
                    ));
                }
            } else {
                if (request.creationDateTimeAfter() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("creationDateTime"), request.creationDateTimeAfter()));
                }
                if (request.creationDateTimeBefore() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("creationDateTime"), request.creationDateTimeBefore()));
                }
            }

            // --- Date Range Filters (Finish Date) ---
            if (request.finishDateTimeAfter() != null && request.finishDateTimeBefore() != null) {
                if (request.finishDateTimeBefore().isAfter(request.finishDateTimeAfter())) {
                    return criteriaBuilder.conjunction(); // Invalid range
                } else {
                    predicates.add(criteriaBuilder.between(
                            root.get("finishDateTime"),
                            request.finishDateTimeAfter(),  // min
                            request.finishDateTimeBefore()   // max
                    ));
                }
            } else {
                if (request.finishDateTimeAfter() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("finishDateTime"), request.finishDateTimeAfter()));
                }
                if (request.finishDateTimeBefore() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("finishDateTime"), request.finishDateTimeBefore()));
                }
            }
            // Combine all accumulated predicates with an AND operator
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
