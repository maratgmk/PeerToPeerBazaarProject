package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSpecifications {
    public static Specification<Product> filterByParams(@Nullable ProductFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filterRequest == null) return criteriaBuilder.conjunction();

            if (filterRequest.category() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), filterRequest.category().name()));
            }
            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }
            if (filterRequest.descriptionKeyWords() != null && !filterRequest.descriptionKeyWords().isEmpty()) {
                filterRequest.descriptionKeyWords().forEach(word -> predicates.add(criteriaBuilder.like(root.get("description"), "%" + word + "%")));
            }
            if (filterRequest.priceHigher() != null) {
                predicates.add(criteriaBuilder.le(root.get("price"), filterRequest.priceHigher()));
            }
            if (filterRequest.priceLower() != null) {
                predicates.add(criteriaBuilder.ge(root.get("price"), filterRequest.priceLower()));
            }
            if (filterRequest.name() != null && !filterRequest.name().isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + filterRequest.name() + "%"));
            }
            if (filterRequest.qrCode() != null && !filterRequest.qrCode().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("qr_code"), filterRequest.qrCode()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
