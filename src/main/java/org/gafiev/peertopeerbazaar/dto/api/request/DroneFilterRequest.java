package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.Set;

/**
 * DTO информация от пользователя для поиска дронов из БД.
 *
 * @param droneServiceIds множество идентификаторов дронов из внешнего сервиса
 * @param droneIds  множество идентификаторов дронов из БД с нашей стороны
 * @param deliveryIdsToRemove множество идентификаторов доставок для удаления
 * @param deliveryIdsToAdd  множество идентификаторов доставок для добавления
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DroneFilterRequest(@Nullable Set<Long> droneServiceIds,
                                 @Nullable Set<Long> droneIds,
                                 Set<@Positive Long> deliveryIdsToRemove,
                                 Set<@Positive Long> deliveryIdsToAdd) {
}

