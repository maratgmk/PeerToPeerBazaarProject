package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.DroneCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;

import java.util.Set;

/**
 * сервис для работы с дронами на нашей стороне
 */

public interface DroneService {

    /**
     * получение дрона по идентификатору из БД.
     * @param id идентификатор дрона
     * @return DTO дрона
     */
    DroneResponse getDroneById(Long id);

    /**
     * получение всех дронов согласно фильтра.
     * @param filterRequest фильтр указывающий параметры или ограничения поиска
     * @return множество DTO дронов
     */
    Set<DroneResponse> getAllDrones(DroneFilterRequest filterRequest);

    /**
     * метод для обновления дронов (для добавления или удаления доставок).
     * @param droneRequest DTO информация для обновления дронов
     * @return DTO обновленный дрон
     */
    DroneResponse update(Long id, DroneCreateRequest droneRequest);

}
