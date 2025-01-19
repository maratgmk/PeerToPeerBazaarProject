package org.gafiev.peertopeerbazaar.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.BasketResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.mapper.BasketMapper;
import org.gafiev.peertopeerbazaar.repository.BasketRepository;
import org.gafiev.peertopeerbazaar.repository.SellerOfferRepository;
import org.gafiev.peertopeerbazaar.service.interfaces.BasketService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper;
    private final SellerOfferRepository sellerOfferRepository;

    /**
     * получение корзины по id
     * @param basketId идентификатор корзины
     * @return DTO корзины
     */
    @Override
    public BasketResponse get(Long basketId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new EntityNotFoundException(Basket.class, Map.of("basketId", String.valueOf(basketId))));
        return basketMapper.toBasketResponse(basket);
    }

    /**
     * добавление в корзину partOfferToBuy по id покупателя, id оффера продавца и желаемого количества продукта
     * @param basketId, sellerOfferId, unitCount соответственно id покупателя, id оффера продавца, количество единиц измерения
     * @return DTO корзины
     */
    @Override
    @Transactional
    public BasketResponse addPartOfferToBuy(Long basketId, Long sellerOfferId, Integer unitCount) {
        Basket basket = basketRepository.findByIdWithPartOfferToBuy(basketId)
                .orElseThrow(() -> new EntityNotFoundException(Basket.class, Map.of("basketId", String.valueOf(basketId))));
        SellerOffer sellerOffer = sellerOfferRepository.findById(sellerOfferId)
                .orElseThrow(() -> new EntityNotFoundException(SellerOffer.class, Map.of("sellerOfferId", String.valueOf(sellerOfferId))));
        if(sellerOffer.getActualUnitCount() < unitCount) throw new IllegalBusinessStateException("Cannot add %d unit to basket: actual amount is less".formatted(unitCount));
        PartOfferToBuy partOfferToBuy = new PartOfferToBuy(null, unitCount, sellerOffer, null, basket);
        basket.addPartOfferToBuy(partOfferToBuy);
        basket = basketRepository.save(basket);
        return basketMapper.toBasketResponse(basket);
    }

    /**
     * удаление из корзины partOfferToBuy по id покупателя и по id части предложения продавца
     * @param basketId, partOfferToBuyId  id покупателя
     * @return DTO корзины
     */
    @Override
    @Transactional
    public BasketResponse removePartOfferToBuy(Long basketId, Long partOfferToBuyId) {
        Basket basket = basketRepository.findByIdWithPartOfferToBuy(basketId)
                .orElseThrow(() -> new EntityNotFoundException(Basket.class, Map.of("basketId", String.valueOf(basketId))));
        PartOfferToBuy partToRemove = basket.getPartOfferToBuySet().stream().filter(part -> part.getId().equals(partOfferToBuyId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(PartOfferToBuy.class, Map.of("partOfferToBuyId", String.valueOf(partOfferToBuyId))));
        basket.removePartOfferToBuy(partToRemove);
        basket = basketRepository.save(basket);
        return basketMapper.toBasketResponse(basket);
    }

    /**
     * очищение корзины по id покупателя
     * @param basketId идентификатор покупателя
     * @return DTO корзины
     */
    @Override
    @Transactional
    public BasketResponse clear(Long basketId) {
        Basket basket = basketRepository.findByIdWithPartOfferToBuy(basketId)
                .orElseThrow(() -> new EntityNotFoundException(Basket.class, Map.of("basketId", String.valueOf(basketId))));
        Set<PartOfferToBuy> parts = basket.getPartOfferToBuySet();
        parts.forEach(basket::removePartOfferToBuy);
        basket = basketRepository.save(basket);
        return basketMapper.toBasketResponse(basket);
    }
}
