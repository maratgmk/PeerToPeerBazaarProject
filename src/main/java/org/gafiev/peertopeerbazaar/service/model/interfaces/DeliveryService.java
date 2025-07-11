package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryUpdateTime;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;

import java.util.List;
import java.util.Set;


public interface DeliveryService {

    /**
     * получение доставки из БД по идентификатору.
     * @param id идентификатор доставки
     * @return DTO доставки
     */
    DeliveryResponse getDeliveryById(Long id);

    /**
     * получение множества доставок по идентификатору заказа покупателя.
     * @param buyerOrderId  идентификатор заказа покупателя
     * @return множество DTO доставок
     */
    Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(Long buyerOrderId);

    /**
     * получение множества поставок из БД с одинаковыми статусами, с одинаковым адресом забора и с одинаковым адресом доставки,
     * согласно временного диапазона между двумя разными ожидаемыми временами доставок.
     * @param deliveryStatus статус доставки
     * @param toAddress адрес получения доставки
     * @param fromAddress адрес забора доставки
     * @param timeSlot  временной диапазон доставок
     * @return подмножество из множества всех доставок, выбранное по указанным параметрам
     */
    Set<DeliveryResponse> getAllDeliveries(DeliveryStatus deliveryStatus,
                                           Address toAddress,
                                           Address fromAddress,
                                           TimeSlot timeSlot);

    /**
     * получение DTO доставок по заданному клиентом в запросе фильтру.
     * @param filterRequest фильтр задающий параметры поиска и ограничений
     * @return множество всех DTO доставок, полученных по этому фильтру
     */
    Set<DeliveryResponse> getAllDeliveriesByFilter(DeliveryFilterRequest filterRequest);


    /**
     * создать доставку.
     * @param request данные от покупателя для доставки.
     * @return доставка до покупателя.
     */
    DeliveryResponse create(DeliveryCreateRequest request);

    /**
     * запрос покупателем доступного времени доставки от внешнего сервиса.
     * @param id идентификатор доставки.
     * @return список временных диапазонов.
     */
    List<TimeSlotResponse> takeTimeSlots(Long id);

    /**
     * создание новой доставки вместо неудавшейся.
     * меняется только Id все параметры остаются прежними
     * @param failDeliveryId идентификатор неудавшейся доставки
     * @return DTO новой попытки доставки вместо неудавшейся попытки
     */
    DeliveryResponse createDeliveryDependsOnFail(Long failDeliveryId);

    /**
     * Установка требуемого временного интервала для существующей (созданной) доставки.
     * @param id идентификатор доставки, которую следует обновить
     * @param updateRequest DTO информация от покупателя на установку временного интервала
     * @return DTO обновленной доставки
     */
    DeliveryResponse assignDroneForDelivery(Long id, DeliveryUpdateTime updateRequest);

    /**
     * метод обновления статуса у delivery и изменения рейтинга продавца и покупателя.
     * @param id идентификатор delivery
     * @param status доставки
     * @return DTO доставки
     */
    DeliveryResponse updateStatus(Long id, DeliveryStatus status);

    /**
     * отмена существующей доставки по требованию покупателя.
     *
     * @param id идентификатор доставки покупателя
     * @return DTO обновленной доставки
     */
    DeliveryResponse cancelMyDelivery(Long id);

    /**
     * удаление доставки по идентификатору из БД.
     * @param id идентификатор доставки
     */
    void deleteDelivery(Long id);

}
