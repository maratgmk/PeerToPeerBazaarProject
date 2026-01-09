package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for BuyerOrder entity.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor for dynamic queries.
 */
@Repository
public interface BuyerOrderRepository extends JpaRepository<BuyerOrder, Long>, JpaSpecificationExecutor<BuyerOrder> {

    /**
     * Retrieves BuyerOrder by identifier, eagerly fetching associated Set of Delivery entities.
     *
     * @param id Unique BuyerOrder identifier.
     * @return Optional containing BuyerOrder entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"deliverySet"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithDelivery(Long id);

    /**
     * Retrieves BuyerOrder by identifier, eagerly fetching associated buyer (User entity).
     *
     * @param id Unique BuyerOrder identifier.
     * @return Optional containing BuyerOrder entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"buyer"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithBuyer(Long id);

    /**
     * Retrieves BuyerOrder by identifier, eagerly fetching associated Set of Delivery entities along with their Address entities.
     *
     * @param id Unique BuyerOrder identifier.
     * @return Optional containing BuyerOrder entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"deliverySet", "deliverySet.address"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithDeliveryAndAddress(Long id);

    /**
     * Retrieves a BuyerOrder by identifier, eagerly fetching the associated set of PartOfferToBuy entities,
     * along with their SellerOffer entities and the SellerOffers' Address entities, and Payment entity.
     *
     * @param id Unique BuyerOrder identifier.
     * @return Optional containing BuyerOrder entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet", "partOfferToBuySet.sellerOffer", "partOfferToBuySet.sellerOffer.address", "payment", "deliverySet"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithPartOfferToBuyAndWithSellerOfferWithAddressAndDeliverySet(Long id);

    /**
     * Retrieves a BuyerOrder by identifier, eagerly fetching the associated set of PartOfferToBuy entities
     * and the set of Delivery entities along with their Address entities.
     *
     * @param id Unique BuyerOrder identifier.
     * @return Optional containing BuyerOrder entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet", "deliverySet", "deliverySet.address"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithDeliveryAndAddressAndPartOfferToBuy(Long id);
}

