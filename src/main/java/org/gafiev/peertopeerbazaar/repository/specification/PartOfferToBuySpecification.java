package org.gafiev.peertopeerbazaar.repository.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.PartOfferToBuyFilterRequest;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.springframework.data.jpa.domain.Specification;
/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartOfferToBuySpecification {
    public static Specification<PartOfferToBuy> filterByParams(PartOfferToBuyFilterRequest filterRequest){
        return (root, query, criteriaBuilder) -> {
            if(filterRequest == null) return criteriaBuilder.conjunction();

            return null;
        };
    }

}
