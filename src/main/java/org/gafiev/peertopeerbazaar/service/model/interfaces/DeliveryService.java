package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;

import java.time.LocalDateTime;
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
     * @param start начальное время временного диапазона
     * @param end конечное время временного диапазона
     * @return подмножество из множества всех доставок, выбранное по указанным параметрам
     */
    Set<DeliveryResponse> getAllDeliveries(DeliveryStatus deliveryStatus,
                                             Address toAddress,
                                             Address fromAddress,
                                             LocalDateTime start,
                                             LocalDateTime end);

    /**
     * получение DTO доставок по заданному клиентом в запросе фильтру.
     * @param filterRequest фильтр задающий параметры поиска и ограничений
     * @return множество всех DTO доставок, полученных по этому фильтру
     */
    Set<DeliveryResponse> getAllDeliveriesByFilter(DeliveryFilterRequest filterRequest);

    /**
     * создание новой доставки.
     * @param delivery DTO информация от покупателя на доставку заказа
     * @return DTO созданной доставки
     */
    DeliveryResponse createDelivery(DeliveryCreateRequest delivery);

    /**
     * создание новой доставки вместо неудавшейся.
     * меняется только Id все параметры остаются прежними
     * @param failDeliveryId идентификатор неудавшейся доставки
     * @return DTO новой попытки доставки вместо неудавшейся попытки
     */
    DeliveryResponse createDeliveryDependsOnFail(Long failDeliveryId);

    /**
     * обновление существующей доставки по требованию администратора.
     * @param id идентификатор доставки, которую следует обновить
     * @param deliveryNew DTO информация от администратора на изменения доставки
     * @return DTO обновленной доставки
     */
    DeliveryResponse updateDeliveryDetails(Long id, DeliveryCreateRequest deliveryNew);

    /**
     * обновление существующей доставки по требованию покупателя.
     *
     * @param id идентификатор доставки покупателя
     * @param userId идентификатор покупателя
     * @param deliveryNew DTO информация от покупателя, которую надо внедрить для обновления существующего заказа
     * @return DTO обновленной доставки
     */
    DeliveryResponse updateMyDeliveryDetails(Long id, Long userId, DeliveryCreateRequest deliveryNew);

    /**
     * удаление доставки по идентификатору из БД.
     * @param id идентификатор доставки
     */
    void deleteDelivery(Long id);

}
