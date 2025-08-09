package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    /**
     * чтобы обойти FETCH.LAZY
     * запрос JPQL в методе поиска basket вместе с partOfferToBuySet
     *
     * @param userId это basketIds (userId)
     * @return basket, если она есть
     */
    @Query("SELECT b FROM Basket b LEFT JOIN FETCH b.partOfferToBuySet WHERE b.id = :userId")
    Optional<Basket> findByIdWithPartOfferToBuy(@Param("userId") Long userId);

    /**
     * Получение корзины со всеми выбранными частями разных офферов и соответствующего оффера к каждой части.
     * @param userId идентификатор пользователя
     * @return корзину
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet", "partOfferToBuySet.sellerOffer"})
    @Query("SELECT b FROM Basket b WHERE b.id = :userId")
    Optional<Basket> findByIdWithPartOfferToBuySetAndSellerOffer(@Param("userId") Long userId);



    /**
     * Получить корзины, у которых есть части заказа от предложений с любыми из переданных статусов.
     *
     * @param statuses набор статусов OfferStatus
     * @return список корзин с загруженными частями заказа и предложениями (eager fetch)
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet", "partOfferToBuySet.sellerOffer", "partOfferToBuySet.buyerOrder"})
    @Query("SELECT b FROM Basket b " +
            "JOIN b.partOfferToBuySet p " +
            "JOIN p.sellerOffer s " +
            "WHERE s.offerStatus IN :statuses")
    Set<Basket> findBasketsBySellerOfferStatuses(@Param("statuses") Set<OfferStatus> statuses);
}


