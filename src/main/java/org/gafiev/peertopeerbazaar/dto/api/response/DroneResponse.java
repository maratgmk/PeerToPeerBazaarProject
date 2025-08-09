package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

import java.util.Set;

/**
 * ответ из БД на запрос поиска дрона.
 * @param id идентификатор дрона в приложении PTPB.
 * @param droneServiceId идентификатор дрона во внешнем приложении.
 * @param status состояние дрона.
 * @param deliveryIds множество идентификаторов доставок.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record DroneResponse(
        Long id,
        Long droneServiceId,
        DroneStatus status,
        Set<Long> deliveryIds) {
}
