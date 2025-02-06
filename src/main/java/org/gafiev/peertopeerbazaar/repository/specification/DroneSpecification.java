package org.gafiev.peertopeerbazaar.repository.specification;


import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DroneSpecification {
    public static Specification<Drone> filterByParams(@Nullable DroneFilterRequest filterRequest){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filterRequest == null) return criteriaBuilder.conjunction();

            if (filterRequest.droneIds() != null && !filterRequest.droneIds().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.droneIds()));
            }

            if (filterRequest.droneServiceIds() != null && !filterRequest.droneServiceIds().isEmpty()) {
                predicates.add(root.get("droneServiceId").in(filterRequest.droneServiceIds()));
            }
            if (filterRequest.deliveryIdsToRemove() != null && !filterRequest.deliveryIdsToRemove().isEmpty()) {
                predicates.add(root.get("deliverySet").in(filterRequest.deliveryIdsToRemove()));
            }
            if (filterRequest.deliveryIdsToAdd() != null && !filterRequest.deliveryIdsToAdd().isEmpty()) {
                predicates.add(root.get("deliverySet").in(filterRequest.deliveryIdsToAdd()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
