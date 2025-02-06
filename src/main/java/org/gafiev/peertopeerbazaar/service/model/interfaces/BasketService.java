package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;

/**
 * интерфейс описывает методы получения корзины по id,
 * добавление и удаление из корзины части оффера, выбранной покупателем,
 * метод очищения корзины
 */
public interface BasketService {

    BasketResponse get(Long basketId);

    BasketResponse addPartOfferToBuy(Long basketId, Long sellerOfferId, Integer unitCount);

    BasketResponse removePartOfferToBuy(Long basketId, Long partOfferToBuyId);

    BasketResponse clear(Long basketId);//TODO на будущее сделать задачу очистки корзины по расписанию,
    //TODO если корзина наполнена, а оплаты не было, то по расписанию удалить части заказа с закрытыми офферами
}
