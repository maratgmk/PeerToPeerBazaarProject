package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
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
public class BuyerOrderSpecification {
    /**
     * Creates a JPA Specification to filter BuyerOrder entities based on the provided criteria.
     * The specification dynamically builds a list of predicates which are combined using AND logic.
     *
     * Criteria applied include:
     * - `ids`: Matches buyer orders whose primary ID is within the provided set.
     * - `partOfferToBuyIds`: Matches buyer orders linked to parts within the provided set of partOfferToBuy IDs.
     * - `deliveryIds`: Matches buyer orders linked to deliveries within the provided set of delivery IDs.
     * - `paymentIds`: Matches buyer orders linked to payments within the provided set of payment IDs.
     * - `buyerIds`: Matches buyer orders linked to buyers within the provided set of buyer IDs.
     * - `buyerOrderStatus`: Matches buyer orders by an exact status match.
     *
     * If the filterRequest is null or empty, an "always true" predicate (conjunction) is returned, applying no filters.
     *
     * @param filterRequest BuyerOrderFilterRequest DTO containing filter criteria (can be null).
     * @return Specification<BuyerOrder> for use with JpaSpecificationExecutor.
     */
    public static Specification<BuyerOrder> filterByParams(@Nullable BuyerOrderFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            // Filter by buyer order IDs (mandatory)
            if (!filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            // Filter by part offer to buy IDs (optional)
            if (filterRequest.partOfferToBuyIds() != null && !filterRequest.partOfferToBuyIds().isEmpty()) {
                Join<BuyerOrder, PartOfferToBuy> partOfferJoin = root.join("partOfferToBuySet", JoinType.LEFT);
                predicates.add(partOfferJoin.get("id").in(filterRequest.partOfferToBuyIds()));
            }

            // Filter by delivery IDs (optional)
            if (filterRequest.deliveryIds() != null && !filterRequest.deliveryIds().isEmpty()) {
                Join<BuyerOrder, Delivery> deliveryJoin = root.join("deliverySet", JoinType.LEFT);
                predicates.add(deliveryJoin.get("id").in(filterRequest.deliveryIds()));
            }

            // Filter by buyer order status (optional)
            if (filterRequest.buyerOrderStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("buyerOrderStatus"), filterRequest.buyerOrderStatus().name()));
            }

            // Filter by buyer IDs (optional)
            if (filterRequest.buyerIds() != null && !filterRequest.buyerIds().isEmpty()) {
                predicates.add(root.get("buyer").get("id").in(filterRequest.buyerIds()));
            }

            // Filter by payment IDs (optional)
            if (filterRequest.paymentIds() != null && !filterRequest.paymentIds().isEmpty()) {
                predicates.add(root.get("payment").get("id").in(filterRequest.paymentIds()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}