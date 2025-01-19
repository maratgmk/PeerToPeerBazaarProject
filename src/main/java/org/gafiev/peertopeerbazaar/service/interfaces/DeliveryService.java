package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.response.DeliveryResponse;

import java.util.Set;


public interface DeliveryService {

    DeliveryResponse getDeliveryById(Long id);

    Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(Long buyerOrderId);

    //TODO фильтрация по заданным признакам (по заданному периоду времени, заданному статусу, по адресу to from ,  по buyer) в репозитории через запросы SQL
    Set<DeliveryResponse> getAllDeliveries();

    DeliveryResponse createDelivery(DeliveryCreateRequest delivery);

    DeliveryResponse createDeliveryDependsOnFail(Long failDeliveryId); // меняется только Id все параметры остаются прежними


    DeliveryResponse updateDeliveryDetails(Long id, DeliveryCreateRequest deliveryNew); // для администраторов

    DeliveryResponse updateMyDeliveryDetails(Long id, Long userId, DeliveryCreateRequest deliveryNew);

    void deleteDelivery(Long id);

}
