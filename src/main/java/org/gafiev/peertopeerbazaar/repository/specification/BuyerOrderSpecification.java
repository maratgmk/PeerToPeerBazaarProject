package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuyerOrderSpecification {
    public static Specification<BuyerOrder> filterByParams(@Nullable BuyerOrderFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            if (filterRequest.partOfferToBuyIds() != null && !filterRequest.partOfferToBuyIds().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.partOfferToBuyIds()));
            }

            if (filterRequest.deliveryIds() != null && !filterRequest.deliveryIds().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.deliveryIds()));
            }

            if (filterRequest.buyerOrderStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("buyer_order_status"), filterRequest.buyerOrderStatus().name()));
            }

            if (filterRequest.buyerIds() != null && filterRequest.buyerIds().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterRequest.buyerIds()));
            }

            if (filterRequest.paymentIds() != null && filterRequest.paymentIds().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filterRequest.paymentIds()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
