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
 * Utility class for creating JPA Specifications to filter Address entities dynamically
 * based on criteria provided in a AddressFilterRequest DTO.
 * This utilizes the Spring Data JPA Specification interface in conjunction with JpaSpecificationExecutor
 * to build dynamic query predicates.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressSpecifications {
    /**
     * Creates a JPA Specification to filter Address entities based on the provided criteria.
     * The specification dynamically builds a list of predicates which are combined using AND logic.
     *
     * Criteria applied include:
     * - `ids`: Matches addresses whose primary ID is within the provided set.
     * - `town`: Matches addresses by an exact town name match.
     * - `street`: Matches addresses by an exact street name match.
     * - `numbers`: Matches addresses whose building number is within the provided set.
     * - Longitude Ranges: Filters addresses based on left/right coordinate ranges.
     * - Latitude Ranges: Filters addresses based on south/north coordinate ranges.
     * - Altitude Ranges: Filters addresses based on low/high altitude ranges.
     *
     * If the request is null or empty, an "always true" predicate (conjunction) is returned, applying no filters.
     *
     * @param request AddressFilterRequest DTO containing filter criteria (can be null).
     * @return Specification<Address> for use with JpaSpecificationExecutor.
     */
    public static Specification<Address> filterByParams(@Nullable AddressFilterRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request == null) return criteriaBuilder.conjunction();

            // Filter by specific address IDs if provided
            if (!request.ids().isEmpty()) {
                predicates.add(root.get("id").in(request.ids()));
            }
            // Filter by maximum longitude (longitude <= longitudeRight)
            if (request.longitudeRight() != null) {
                predicates.add(criteriaBuilder.le(root.get("longitude"), request.longitudeRight()));
            }
            // Filter by minimum longitude (longitude >= longitudeLeft)
            predicates.add(criteriaBuilder.ge(root.get("longitude"), request.longitudeLeft()));

            // Filter by maximum latitude (latitude <= latitudeNorth)
            predicates.add(criteriaBuilder.le(root.get("latitude"), request.latitudeNorth()));

            // Filter by minimum latitude (latitude >= latitudeSouth)
            predicates.add(criteriaBuilder.ge(root.get("latitude"), request.latitudeSouth()));

            // Filter by maximum altitude (altitude <= altitudeHigh)
            predicates.add(criteriaBuilder.le(root.get("altitude"), request.altitudeHigh()));

            // Filter by minimum altitude (altitude >= altitudeLow)
            predicates.add(criteriaBuilder.ge(root.get("altitude"), request.altitudeLow()));

            // Filter by exact town name match
            if (!request.town().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("town"), request.town()));
            }

            // Filter by exact street name match
            if (!request.street().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("street"), request.street()));
            }

            // Filter by specific building numbers if provided
            if (!request.numbers().isEmpty()) {
                predicates.add(root.get("buildingNumber").in(request.numbers()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
