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

/**
 * Spring Data JPA repository for Basket entity.
 * Extends JpaRepository for basic CRUD operations.
 */
@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

    /**
     * Retrieves Basket by its identifier, eagerly fetching associated Set of PartOfferToBuy parts.
     *
     * @param userId Unique User identifier.
     * @return Optional containing Basket entity if found, otherwise empty.
     */
    @Query("SELECT b FROM Basket b LEFT JOIN FETCH b.partOfferToBuySet WHERE b.id = :userId")
    Optional<Basket> findByIdWithPartOfferToBuy(@Param("userId") Long userId);

    /**
     * Retrieves Basket by its identifier, eagerly fetching associated Set of PartOfferToBuy parts and
     * SellerOffer entity.
     *
     * @param userId Unique User identifier.
     * @return Optional containing Basket entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet", "partOfferToBuySet.sellerOffer"})
    @Query("SELECT b FROM Basket b WHERE b.id = :userId")
    Optional<Basket> findByIdWithPartOfferToBuySetAndSellerOffer(@Param("userId") Long userId);

    /**
     * Retrieves Basket entities by associated SellerOffer statuses.
     *
     * @param statuses Set of SellerOffer statuses.
     * @return Set of Basket entities matching criteria.
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet", "partOfferToBuySet.sellerOffer"})
    @Query("SELECT b FROM Basket b " +
            "JOIN b.partOfferToBuySet p " +
            "JOIN p.sellerOffer s " +
            "WHERE s.offerStatus IN :statuses")
    Set<Basket> findBasketsBySellerOfferStatuses(@Param("statuses") Set<OfferStatus> statuses);
}


