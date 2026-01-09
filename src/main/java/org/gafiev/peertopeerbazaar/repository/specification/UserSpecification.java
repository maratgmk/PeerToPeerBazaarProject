package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating JPA Specifications to filter User entities dynamically
 * based on criteria provided in a UserFilterRequest DTO.
 * This utilizes the Spring Data JPA Specification interface in conjunction with JpaSpecificationExecutor
 * to build dynamic query predicates.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {
    /**
     * Creates a JPA Specification to filter User entities based on the provided UserFilterRequest criteria.
     * The specification builds a list of predicates for filtering by IDs, roles, and rating ranges (buyer and seller).
     * If the filterRequest is null, it returns a conjunction (no filtering).
     *
     * Filtering logic:
     * - IDs: Filters users whose ID is in the provided set of IDs.
     * - Roles: Performs an INNER JOIN on the 'roles' collection and checks if any of the user's roles match the filter roles.
     * - Rating Buyer Low: Filters users with buyer rating greater than or equal to the specified low value.
     * - Rating Buyer High: Filters users with buyer rating less than or equal to the specified high value.
     * - Rating Seller Low: Filters users with seller rating greater than or equal to the specified low value.
     * - Rating Seller High: Filters users with seller rating less than or equal to the specified high value.
     *
     * All predicates are combined using AND logic.
     *
     * @param filterRequest the UserFilterRequest DTO containing filter criteria (can be null).
     * @return a Specification<User> that can be used with JpaSpecificationExecutor to query filtered User entities.
     */
    public static Specification<User> filterByParams(UserFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            // Initialize a list to hold all predicates
            List<Predicate> predicates = new ArrayList<>();

            // If no filter request is provided, return a conjunction (no filters applied)
            if (filterRequest == null) return criteriaBuilder.conjunction();

            // Filter by IDs: Add predicate if IDs list is provided and not empty
            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            // Filter by roles: Perform INNER JOIN on 'roles' collection and check for matches
            // This ensures the user has at least one role from the filter list
            if (filterRequest.roles() != null && !filterRequest.roles().isEmpty()) {
                Join<User, Role> rolesJoin = root.join("roles", JoinType.INNER);  // <-- Ключ: JOIN на коллекцию
                predicates.add(rolesJoin.in(filterRequest.roles()));  // <-- Это Predicate (один аргумент для add)
            }

            // Filter by buyer rating low: >= low value
            if (filterRequest.ratingBuyerLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("ratingBuyer"), filterRequest.ratingBuyerLow()));
            }

            // Filter by buyer rating high: <= high value
            if (filterRequest.ratingBuyerHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("ratingBuyer"), filterRequest.ratingBuyerHigh()));
            }

            // Filter by seller rating low: >= low value
            if (filterRequest.ratingSellerLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("ratingSeller"), filterRequest.ratingSellerLow()));
            }

            // Filter by seller rating high: <= high value
            if (filterRequest.ratingSellerHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("ratingSeller"), filterRequest.ratingSellerHigh()));
            }

            // Combine all predicates with AND and return
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
