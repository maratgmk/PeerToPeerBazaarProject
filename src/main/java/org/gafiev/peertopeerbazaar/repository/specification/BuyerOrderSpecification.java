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
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuyerOrderSpecification {
    public static Specification<BuyerOrder> filterByParams(@Nullable BuyerOrderFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            // Фильтр по ID заказов (обязательный)
            if (!filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            // Фильтр по ID частей предложений (опциональный)
            if (filterRequest.partOfferToBuyIds() != null && !filterRequest.partOfferToBuyIds().isEmpty()) {
                Join<BuyerOrder, PartOfferToBuy> partOfferJoin = root.join("partOfferToBuySet", JoinType.LEFT);
                predicates.add(partOfferJoin.get("id").in(filterRequest.partOfferToBuyIds()));
            }

            // Фильтр по ID доставок (опциональный)
            if (filterRequest.deliveryIds() != null && !filterRequest.deliveryIds().isEmpty()) {
                Join<BuyerOrder, Delivery> deliveryJoin = root.join("deliverySet", JoinType.LEFT);
                predicates.add(deliveryJoin.get("id").in(filterRequest.deliveryIds()));
            }

            // Статус заказа (опциональный)
            if (filterRequest.buyerOrderStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("buyerOrderStatus"), filterRequest.buyerOrderStatus().name()));
            }
            // Фильтр по ID покупателей (опциональный)
            if (filterRequest.buyerIds() != null && !filterRequest.buyerIds().isEmpty()) {
                predicates.add(root.get("buyer").get("id").in(filterRequest.buyerIds()));
            }

            // Фильтр по ID платежей (опциональный)
            if (filterRequest.paymentIds() != null && !filterRequest.paymentIds().isEmpty()) {
                predicates.add(root.get("payment").get("id").in(filterRequest.paymentIds()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}