package org.gafiev.peertopeerbazaar.service.integration.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalDroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;

import java.util.Set;


public interface ExternalDroneService {
    /**
     * получение информации от внешнего сервиса по идентификатору дрона от внешнего сервиса.
     * @param droneServiceId идентификатор дрона от внешнего сервиса
     * @return DTO информация о дроне от внешнего сервиса
     */
    ExternalDroneResponse getDroneById(Long droneServiceId);

    /**
     * получение множества всех DTO дронов по фильтру от внешнего сервиса.
     * @param filterRequest фильтр, задающий параметры поиска и отбора дронов из внешнего сервиса
     * @return множество DTO информаций о дронах от внешнего сервиса
     */
    Set<ExternalDroneResponse> getAllDronesExternal(ExternalDroneFilterRequest filterRequest);

    /**
     * получение дрона от внешнего сервиса по данным о доставке
     * @param deliveryDroneRequest запрос дрона по данным о доставке
     * @return получение DTO дрона от внешнего сервиса
     */
    ExternalDroneResponse requestDrone(DeliveryDroneRequest deliveryDroneRequest);

    /**
     * получение множества доступных временных диапазонов.
     * @param deliveryDroneRequest запрос дрона по данным о доставке
     * @return множество временных диапазонов
     */
    Set<TimeSlotResponse> requestDroneSchedule(DeliveryDroneRequest deliveryDroneRequest);

    /**
     * получение информации о доступности обслуживания адреса
     * @param addressCreateRequest запрос на (проверку) создание адреса
     * @return код доступности адреса (код перечисления)
     */
    String getCode(AddressCreateRequest addressCreateRequest);

    /**
     * отмена дрона по идентификатору от внешнего сервиса.
     * @param droneServiceId идентификатор дрона от внешнего сервиса
     * @return ответ от внешнего сервиса
     */
    ExternalDroneResponse cancelDrone(Long droneServiceId);

}

//   TODO добавить метод для отмены бронирования дронов здесь и в стороннем сервисе
