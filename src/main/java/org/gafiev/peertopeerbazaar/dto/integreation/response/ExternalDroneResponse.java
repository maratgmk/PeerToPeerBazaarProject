package org.gafiev.peertopeerbazaar.dto.integreation.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

/**
 * информация о дроне от внешнего сервиса.
 * @param droneServiceId идентификатор дрона от внешнего сервиса
 * @param droneStatus состояние дрона от внешнего сервиса
 * @param errorMessage сообщение об ошибке от внешнего сервиса
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record ExternalDroneResponse(
        Long droneServiceId,
        DroneStatus droneStatus,
        String  errorMessage) {
}
