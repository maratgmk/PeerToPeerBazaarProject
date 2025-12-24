package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for SellerOffer entity.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor for dynamic queries.
 */
@Repository
public interface SellerOfferRepository extends JpaRepository<SellerOffer, Long>, JpaSpecificationExecutor<SellerOffer> {

    /**
     * Retrieves SellerOffer by its identifier, eagerly fetching the associated List of PartOfferToBuy entities.
     *
     * @param id Unique SellerOffer identifier.
     * @return Optional containing SellerOffer entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"partOfferToBuyList"})
    @Query("SELECT o FROM SellerOffer o WHERE o.id = :id")
    Optional<SellerOffer> findByIdWithPartOfferToBuy(Long id);

    /**
     * Retrieves SellerOffer by its identifier, eagerly fetching the associated seller (User) entity.
     *
     * @param id Unique SellerOffer identifier.
     * @return Optional containing SellerOffer entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"seller"})
    @Query("SELECT o FROM SellerOffer o WHERE o.id = :id")
    Optional<SellerOffer> findByIdWithSeller(Long id);

    /**
     * Retrieves SellerOffer by the related product, address, seller Ids, eagerly fetching the associated Product, Address and User (seller) entities.
     *
     * @param productId Unique Product identifier.
     * @param addressId Unique Address identifier.
     * @param sellerId Unique User identifier.
     * @return Optional containing SellerOffer entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"product","address","seller"})
    @Query("SELECT o FROM SellerOffer o WHERE o.product.id = :productId AND o.address.id = :addressId AND o.seller.id = :sellerId")
    Optional<SellerOffer> findByProductIdAndAddressIdAndSellerId(
            @Param("productId") Long productId,
            @Param("addressId") Long addressId,
            @Param("sellerId") Long sellerId);
}

