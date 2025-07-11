package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;

/**
 * сервис для периодических запросов информации о дронах.
 */
public interface ScheduledDroneService {
    /**
     * Получить состояние дрона.
     * @param droneServiceId идентификатор дрона от внешнего сервиса.
     * @return информацию о состоянии дрона.
     */
    ExternalDroneResponse getDroneStatus(Long droneServiceId);
}
