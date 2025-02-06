package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.SellerOfferResponse;

import java.util.Set;

/**
 * интерфейс по работе с DTO оффер продавца,
 * создание нового оффера продавца, изменение существующего оффера продавца, удаление оффера продавца
 * получение оффера продавца по его Id, получение всех офферов продавца по его клиентскому Id,
 * получение множества офферов продавца согласно переданного фильтра в запросе
 */
public interface SellerOfferService {

    SellerOfferResponse getSellerOfferById(Long id);

    SellerOfferResponse getSellerOfferByIdWithPartOfferToBuy(Long id);

    Set<SellerOfferResponse> getAllMySellerOffers(Long userId);

    Set<SellerOfferResponse> getAllSellerOffers(SellerOfferFilterRequest filterRequest);

    SellerOfferResponse createSellerOffer(Long sellerId, SellerOfferCreateRequest sellerOfferCreate);

    SellerOfferResponse updateMySellerOffer(Long sellerId, Long id,  SellerOfferCreateRequest sellerOfferNew);

    void deleteSellerOffer(Long id);

}
