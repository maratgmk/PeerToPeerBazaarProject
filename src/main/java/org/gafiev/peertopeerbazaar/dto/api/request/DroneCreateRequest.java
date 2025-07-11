package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;

import java.util.Set;

/**
 * запрос на обновление дрона, указав, что ему забрать, а что оставить.
 * @param deliveryIdsToRemove множество доставок, которые необходимо удалить, чтобы дрон не обслуживал их.
 * @param deliveryIdsToAdd множество доставок, которые дрон должен забрать.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DroneCreateRequest(
        Set<@Positive Long>  deliveryIdsToRemove,
        Set<@Positive Long>  deliveryIdsToAdd ) {
}
