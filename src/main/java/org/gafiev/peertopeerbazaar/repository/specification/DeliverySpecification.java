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
 * Utility class for building dynamic JPA Specifications to filter Delivery entities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliverySpecification {

    /**
     * Creates a JPA Specification to filter Delivery entities based on the provided criteria.
     *
     * Criteria applied:
     * - ids: Matches deliveries within the specified set of IDs.
     * - deliveryStatus: Matches an exact delivery status.
     * - startTimeAfter: Filters deliveries based on the start boundary of the range.
     * - endTimeBefore: Filters deliveries based on the end boundary of the range.
     * - entirelyWithin: Switches between strict containment and overlap logic for time filtering.
     * - buyerOrderId: Matches deliveries associated with a specific buyer order.
     * - fromAddressId: Matches deliveries departing from a specific origin address.
     * - toAddressId: Matches deliveries arriving at a specific destination address.
     * - droneId: Matches deliveries assigned to a specific drone.
     *
     * If the request is null or empty, an "always true" predicate (conjunction) is returned, applying no filters.
     *
     * @param filterRequest DTO containing filter criteria (can be null).
     * @return Specification for filtering Delivery entities.
     */
    public static Specification<Delivery> filterByParams(@Nullable DeliveryFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            if (filterRequest == null) return criteriaBuilder.conjunction();

            List<Predicate> predicates = new ArrayList<>();

            // Match by a set of delivery identifiers
            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            // Match by the current delivery status
            if (filterRequest.deliveryStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("deliveryStatus"), filterRequest.deliveryStatus()));
            }

            if (filterRequest.entirelyWithin()) {
                // "Subset" Mode: Matches deliveries fully contained within the requested range.
                // Used for scheduling and finding free time slots.
                if (filterRequest.startTimeAfter() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                            root.get("timeSlot").get("start"), filterRequest.startTimeAfter().getStart()));
                }
                if (filterRequest.endTimeBefore() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                            root.get("timeSlot").get("end"), filterRequest.endTimeBefore().getEnd()));
                }
            } else {
                // "Intersection" Mode (Default): Matches any delivery active during the requested range.
                // Used for monitoring all ongoing activity within the interval
                if (filterRequest.startTimeAfter() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                            root.get("timeSlot").get("end"), filterRequest.startTimeAfter().getStart()));
                }
                if (filterRequest.endTimeBefore() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                            root.get("timeSlot").get("start"), filterRequest.endTimeBefore().getEnd()));
                }
            }

            // Match by the associated buyer order ID
            if (filterRequest.buyerOrderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("buyerOrder").get("id"), filterRequest.buyerOrderId()));
            }

            // Match by the assigned drone ID
            if (filterRequest.droneId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("drone").get("id"), filterRequest.droneId()));
            }

            // Match by the origin address ID (pickup point)
            if (filterRequest.fromAddressId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("fromAddress").get("id"), filterRequest.fromAddressId()));
            }

            // Match by the destination address ID
            if (filterRequest.toAddressId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("toAddress").get("id"), filterRequest.toAddressId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
