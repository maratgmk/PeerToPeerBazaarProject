package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;

/**
 * интерфейс описывает методы получения заказа покупателя по id покупателя и id заказа покупателя,
 * метод получения всех заказов покупателя по id покупателя и согласно статуса заказов покупателя,
 * метод создания заказа покупателя,
 * удаление заказа покупателя, метод отмены конкретного заказа покупателя
 */
public interface BuyerOrderService {

    /**
     * метод получения DTO заказа покупателя по двум идентификаторам.
     *
     * @param buyerId  идентификатор покупателя
     * @param buyerOrderId идентификатор заказа покупателя
     * @return buyerOrderResponse
     */
    BuyerOrderResponse get(Long buyerId, Long buyerOrderId);

    /**
     * метод получения множества DTO заказов покупателя, соответствующих определённому статусу.
     *
     * @param buyerId идентификатор покупателя
     * @param buyerOrderStatus состояние заказа покупателя
     * @return buyerOrderResponseSet
     */
    Set<BuyerOrderResponse> getAll(Long buyerId, BuyerOrderStatus buyerOrderStatus);

    /**
     * поиск всех заказов покупателя, удовлетворяющих заданным критериям.
     * @param filterRequest фильтр по которому происходит выборка заказов покупателя
     * @return множество DTO заказов покупателя
     */
    Set<BuyerOrderResponse> getAllBuyerOrders(BuyerOrderFilterRequest filterRequest);

    /**
     * метод создания множества новых заказов покупателя.
     *
     * @param buyerId идентификатор покупателя
     * @param candidate информация от покупателя, необходимая для создания нового заказа
     * @return множество заказов разделённых по поставщикам, адресам забора товаров дронами, временем забора товаров
     */
    Set<BuyerOrderResponse> create(Long buyerId, BuyerOrderCreateRequest candidate);

    /**
     *
     * @param buyerId
     * @param requestNew
     * @return
     */
    BuyerOrderResponse update(Long buyerId, Long buyerOrderId, BuyerOrderUpdateRequest requestNew);

    /**
     * метод отмены заказа покупателя.
     *
     * @param buyerId идентификатор покупателя
     * @param buyerOrderId идентификатор заказа покупателя
     */
    void cancel(Long buyerId, Long buyerOrderId);

    /**
     * метод удаления заказа покупателя из БД.
     *
     * @param buyerId идентификатор покупателя
     * @param buyerOrderId идентификатор заказа покупателя
     */
    void delete(Long buyerId, Long buyerOrderId);

}
