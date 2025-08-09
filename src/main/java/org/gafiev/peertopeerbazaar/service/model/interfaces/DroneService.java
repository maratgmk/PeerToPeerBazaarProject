package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.DroneCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;

import java.util.List;
import java.util.Set;

/**
 * сервис для работы с дронами на стороне приложения PTPB.
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
     * метод для обновления дрона (для добавления или удаления доставок).
     * @param droneRequest DTO информация для обновления дрона
     * @return DTO обновленный дрон
     */
    DroneResponse update(Long id, DroneCreateRequest droneRequest);

    DroneResponse update2(Long id, DroneCreateRequest droneRequest);

    /**
     * получение информации о временных рамках доступности дронов, от внешнего сервиса дронов.
     * @param id идентификатор доставки покупателя.
     * @return список DTO возможных временных диапазонов, предоставляемых внешним сервисом дронов.
     */
    List<TimeSlotResponse> getTimeSlots(Long id);

    /**
     * Наблюдение за местонахождением дрона по статусу состояний, и по координатам (в перспективе).
     * @param id идентификатор дрона.
     * @return DTO дрона с его "местонахождением".
     */
    DroneResponse observingFlightOfDrone(Long id);

    /**
     * метод отмены дрона покупателем по его инициативе.
     * @param id идентификатор дрона в репозитории
     * @return DTO дрона, который решили отменить
     */
    DroneResponse cancelDrone(Long id);
}


