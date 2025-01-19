package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket,Long> {
    /**
     * чтобы обойти FETCH.LAZY
     * запрос JPQL в методе поиска basket вместе с partOfferToBuySet
     * @param id это basketId
     * @return basket, если она есть
     */
    @Query("SELECT b FROM Basket b LEFT JOIN FETCH b.partOfferToBuySet WHERE b.id = :id")
    Optional<Basket> findByIdWithPartOfferToBuy(@Param("id") Long id);
}
