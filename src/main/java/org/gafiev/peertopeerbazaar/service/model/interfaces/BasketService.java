package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;

/**
 * Provides methods to work with Basket data.
 */
public interface BasketService {

    /**
     * Retrieves Basket using its identifier.
     *
     * @param userId Unique identifier of User.
     * @return Basket response DTO.
     */
    BasketResponse get(Long userId);

    /**
     * Reserves specified quantity of PartOfferToBuy entities and adds them to Basket.
     *
     * @param userId Unique identifier of User.
     * @param sellerOfferId Unique identifier of SellerOffer entity.
     * @param unitCount Quantity of PartOfferToBuy entities to reserve and add.
     * @return Basket response DTO.
     */
    BasketResponse addPartOfferToBuy(Long userId, Long sellerOfferId, Integer unitCount);

    /**
     * Removes PartOfferToBuy from Basket.
     *
     * @param userId Unique identifier of User.
     * @param partOfferToBuyId Unique identifier of PartOfferToBuy entity.
     * @return Basket response DTO.
     */
    BasketResponse removePartOfferToBuy(Long userId, Long partOfferToBuyId);
}
