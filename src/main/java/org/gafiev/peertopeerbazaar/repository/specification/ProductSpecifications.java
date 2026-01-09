package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


/**
 * Utility class for creating JPA Specifications to filter Product entities dynamically
 * based on criteria provided in a ProductFilterRequest DTO.
 * This utilizes the Spring Data JPA Specification interface in conjunction with JpaSpecificationExecutor
 * to build dynamic query predicates.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSpecifications {
    /**
     * Creates a JPA Specification to filter Product entities based on the provided ProductFilterRequest criteria.
     * The specification dynamically builds a list of predicates which are combined using AND logic.
     *
     * Filtering logic leverages the fields provided in ProductFilterRequest:
     *
     * IDs: Filters products whose ID is in the provided set of identifiers.
     * Name: Performs a 'LIKE' search for the product name (case-insensitive containment).
     * Description Keywords: Performs 'LIKE' searches for each keyword in the product description.
     * Category: Filters products by an exact match on the product category.
     * Price Higher: Filters products with a price less than or equal to the specified upper limit.
     * Price Lower: Filters products with a price greater than or equal to the specified lower limit.
     * QR Code: Filters products by an exact match on the QR code URL.
     *
     * If the filterRequest is null, a conjunction (no filtering) is returned.
     *
     * @param filterRequest The ProductFilterRequest DTO containing filter criteria (can be null).
     * @return Specification<Product> that can be used with JpaSpecificationExecutor to query filtered Product entities.
     */
    public static Specification<Product> filterByParams(@Nullable ProductFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filterRequest == null) return criteriaBuilder.conjunction();

            // Filters products by an exact match on the product category.
            if (filterRequest.category() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), filterRequest.category().name()));
            }

            // Filters products whose ID is present within the provided set of identifiers
            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            // Performs 'LIKE' searches for each keyword, requiring the description to contain the keyword.
            if (filterRequest.descriptionKeyWords() != null && !filterRequest.descriptionKeyWords().isEmpty()) {
                filterRequest.descriptionKeyWords().forEach(word -> predicates.add(criteriaBuilder.like(root.get("description"), "%" + word + "%")));
            }

            // Filters products with a price less than or equal to the specified upper limit (priceHigher).
            if (filterRequest.priceHigher() != null) {
                predicates.add(criteriaBuilder.le(root.get("price"), filterRequest.priceHigher()));
            }

            // Filters products with a price greater than or equal to the specified lower limit (priceLower).
            if (filterRequest.priceLower() != null) {
                predicates.add(criteriaBuilder.ge(root.get("price"), filterRequest.priceLower()));
            }

            // Performs a 'LIKE' search, requiring the product name to contain the specified string.
            if (filterRequest.name() != null && !filterRequest.name().isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + filterRequest.name() + "%"));
            }

            // Filters products by an exact match on the QR code URL column.
            if (filterRequest.qrCode() != null && !filterRequest.qrCode().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("qr_code"), filterRequest.qrCode()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
