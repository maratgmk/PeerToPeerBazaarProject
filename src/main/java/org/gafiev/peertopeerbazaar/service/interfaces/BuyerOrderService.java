package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;

/**
 * интерфейс описывает методы получения заказа покупателя по id покупателя и id заказа покупателя,
 * метод получения всех заказов покупателя по id покупателя и согласно статуса заказов покупателя,
 * метод создания заказа покупателя,
 * удаление заказа покупателя, метод отмены конкретного заказа покупателя
 */
public interface BuyerOrderService {
    Set<BuyerOrderResponse> create(Long buyerId, BuyerOrderCreateRequest candidate);

    BuyerOrderResponse get(Long buyerId, Long buyerOrderId);

    Set<BuyerOrderResponse> getAll(Long buyerId, BuyerOrderStatus buyerOrderStatus);

    void cancel(Long buyerId, Long buyerOrderId);

    void delete(Long buyerId, Long buyerOrderId);

}
//TODO метод на изменение после создания DTO UpDate