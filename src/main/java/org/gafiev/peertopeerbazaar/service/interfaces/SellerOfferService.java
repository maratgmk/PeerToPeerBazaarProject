package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.SellerOfferResponse;

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

    SellerOfferResponse createSellerOffer(SellerOfferCreateRequest sellerOfferCreate);

    SellerOfferResponse updateMySellerOffer(Long id, SellerOfferCreateRequest sellerOfferNew);

    void deleteSellerOffer(Long id);

}
