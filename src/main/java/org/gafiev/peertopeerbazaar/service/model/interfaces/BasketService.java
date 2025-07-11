package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;

/**
 * интерфейс описывает методы получения корзины по id покупателя.
 * добавление и удаление из корзины части оффера, выбранной покупателем,
 * метод очищения корзины
 */
public interface BasketService {

    BasketResponse get(Long userId);

    BasketResponse addPartOfferToBuy(Long userId, Long sellerOfferId, Integer unitCount);

    BasketResponse removePartOfferToBuy(Long userId, Long partOfferToBuyId);

}
