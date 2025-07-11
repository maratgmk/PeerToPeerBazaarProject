package org.gafiev.peertopeerbazaar.service.model;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;
import org.gafiev.peertopeerbazaar.entity.order.*;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.IllegalBusinessStateException;
import org.gafiev.peertopeerbazaar.mapper.BasketMapper;
import org.gafiev.peertopeerbazaar.repository.BasketRepository;
import org.gafiev.peertopeerbazaar.repository.SellerOfferRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.service.model.interfaces.BasketService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper;
    private final SellerOfferRepository sellerOfferRepository;
    private final UserRepository userRepository;
    public static final Set<OfferStatus> OFFER_STATUSES = Set.of(
            OfferStatus.PRESALE,
            OfferStatus.OPENED,
            OfferStatus.CLOSED
    );

    /**
     * получение корзины по id покупателя.
     *
     * @param userId идентификатор корзины
     * @return DTO корзины
     */
    @Override
    public BasketResponse get(Long userId) {
        Basket basket = basketRepository.findByIdWithPartOfferToBuy(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(userId))));
        return basketMapper.toBasketResponse(basket);
    }

    /**
     * добавление в корзину partOfferToBuy по id покупателя, id оффера продавца и желаемого количества продукта
     *
     * @param userId, sellerOfferId, unitCount соответственно id покупателя, id оффера продавца, количество единиц измерения
     * @return DTO корзины
     */
    @Override
    @Transactional
    public BasketResponse addPartOfferToBuy(Long userId, Long sellerOfferId, Integer unitCount) {
        log.info("Adding part offer to buy: userId={}, sellerOfferId={}, unitCount={}", userId, sellerOfferId, unitCount);
        Basket basket = basketRepository.findByIdWithPartOfferToBuySetAndSellerOffer(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setBuyer(userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(userId)))));

                    return basketRepository.save(newBasket);
                });

        SellerOffer sellerOffer = sellerOfferRepository.findByIdWithPartOfferToBuy(sellerOfferId)
                .orElseThrow(() -> new EntityNotFoundException(SellerOffer.class, Map.of("sellerOfferId", String.valueOf(sellerOfferId))));


        if (sellerOffer.getActualUnitCount() < unitCount)
            throw new IllegalBusinessStateException("Cannot add %d unit to basket: actual amount is less".formatted(unitCount));


        Set<Long> partIds = new HashSet<>();
        for (int i = 0; i < unitCount; i++) {
            PartOfferToBuy partOfferToBuy = sellerOffer.getPartOfferToBuyList().stream()
                    .filter(part -> part.getStatus().equals(PartOfferToBuyStatus.NOT_RESERVED) && !partIds.contains(part.getId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(PartOfferToBuy.class, Map.of("status", String.valueOf(PartOfferToBuyStatus.NOT_RESERVED))));

            basket.addPartOfferToBuy(partOfferToBuy);
            partIds.add(partOfferToBuy.getId());
        }


        basket = basketRepository.save(basket);

        return basketMapper.toBasketResponse(basket);
    }


    /**
     * удаление из корзины partOfferToBuy по id покупателя и по id части предложения продавца
     *
     * @param userId, partOfferToBuyId  id покупателя
     * @return DTO корзины
     */
    @Override
    @Transactional
    public BasketResponse removePartOfferToBuy(Long userId, Long partOfferToBuyId) {
        Basket basket = basketRepository.findByIdWithPartOfferToBuy(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(userId))));
        PartOfferToBuy partToRemove = basket.getPartOfferToBuySet().stream().filter(part -> part.getId().equals(partOfferToBuyId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(PartOfferToBuy.class, Map.of("partOfferToBuyId", String.valueOf(partOfferToBuyId))));
        basket.removePartOfferToBuy(partToRemove);
        basket = basketRepository.save(basket);
        return basketMapper.toBasketResponse(basket);
    }

    /**
     * очищение корзины по расписанию.
     */
    @Scheduled(cron = "${diapason.limit}")
    @Transactional
    public void clear() {
        Set<Basket> baskets = basketRepository.findBasketsBySellerOfferStatuses(OFFER_STATUSES);

        for (Basket basket : baskets) {
            log.info("!!!!! Начало сохранения корзины: basketId = {}, parts = {} " , basket.getId(),basket.getPartOfferToBuySet());

            Iterator<PartOfferToBuy> iterator = basket.getPartOfferToBuySet().iterator();
            while (iterator.hasNext()) {
                PartOfferToBuy partOfferToBuy = iterator.next();
                if (partOfferToBuy.getSellerOffer().getOfferStatus() == OfferStatus.CLOSED) {
                    iterator.remove();
                    partOfferToBuy.getBasketSet().remove(basket);
                }
            }

            log.info("Перед сохранением корзины: basketId = {}, parts = {} " , basket.getId(),basket.getPartOfferToBuySet());
            basket =  basketRepository.save(basket);
            log.info("После сохранения корзины: basketId = {}, parts = {} " , basket.getId(),basket.getPartOfferToBuySet());

        }

        log.info("Корзины очищены от всех частей предложений со статусом OfferStatus.CLOSED");
    }

}
