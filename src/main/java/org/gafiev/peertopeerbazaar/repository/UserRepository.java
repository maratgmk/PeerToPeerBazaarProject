package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 * Extends JPARepository for basic CRUD operations and JpaSpecificationExecutor for dynamic queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Retrieves a User by his email.
     *
     * @param email User's email.
     * @return Optional containing User entity if found, otherwise empty.
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves a User by his ID, eagerly fetching the associated Product, SellerOffer, and BuyerOrder entities.
     *
     * @param id Unique User identifier.
     * @return Optional containing User entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"productSet", "sellerOfferSet", "buyerOrderSet"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdFull(Long id);

    /**
     * Retrieves a User entity, eagerly fetching the associated Product entities.
     *
     * @param id Unique User identifier.
     * @return Optional containing User entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"productSet"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdWithProducts(Long id);


    /**
     * Retrieves a User entity, eagerly fetching the associated BuyerOrder and SellerOffer entities.
     *
     * @param id Unique User identifier.
     * @return Optional containing User entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"buyerOrderSet", "sellerOfferSet"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdWithBuyerOrdersAndSellerOffers(@Param("id") Long id);

    /**
     * Retrieves a User entity, eagerly fetching the associated Basket and its complex nested entities.
     * The fetch joins include:
     *
     * 1. The user's Basket entity.
     * 2. The Set of PartOfferToBuy entities within the basket.
     * 3. The SellerOffer entities associated with the PartOfferToBuy entities.
     * 4. The Address entity associated with the SellerOffer.
     * 5. The Product entity associated with the Address.
     *
     * @param id Unique User identifier.
     * @return Optional containing User entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {
            "basket",
            "basket.partOfferToBuySet",
            "basket.partOfferToBuySet.sellerOffer",
            "basket.partOfferToBuySet.sellerOffer.address",
            "basket.partOfferToBuySet.sellerOffer.product"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdWithBasket(Long id);
}



