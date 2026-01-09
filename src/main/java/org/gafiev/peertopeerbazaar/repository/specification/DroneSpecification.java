package org.gafiev.peertopeerbazaar.repository.specification;


import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for building dynamic JPA Specifications to filter Drone entities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DroneSpecification {

    /**
     * Builds a JPA Specification based on the provided filter criteria.
     *
     * Criteria applied:
     * - droneIds: Matches drones within the specified set of unique internal database identifiers.
     * - droneServiceIds: Matches drones within the specified set of unique identifiers assigned by the external service.
     * - deliveryIds: deliveryIds: Matches drones associated with the specified delivery identifiers.
     *
     * If the request is null or empty, an "always true" predicate (conjunction) is returned, applying no filters.
     *
     * @param filterRequest DTO containing filter criteria (can be null).
     * @return Specification for filtering Drone entities.
     */
    public static Specification<Drone> filterByParams(@Nullable DroneFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            // Filter by internal database identifiers
            if (filterRequest.droneIds() != null && !filterRequest.droneIds().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.droneIds()));
            }

            // Filter by identifiers assigned by the external service
            if (filterRequest.droneServiceIds() != null && !filterRequest.droneServiceIds().isEmpty()) {
                predicates.add(root.get("droneServiceId").in(filterRequest.droneServiceIds()));
            }

            // Filter by associated delivery identifiers
            if (filterRequest.deliveryIds() != null && !filterRequest.deliveryIds().isEmpty()) {
                Objects.requireNonNull(query).distinct(true);
                Join<Drone, Delivery> deliveryJoin = root.join("deliverySet");
                predicates.add(deliveryJoin.get("id").in(filterRequest.deliveryIds()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
