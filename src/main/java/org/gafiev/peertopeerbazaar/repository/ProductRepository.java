package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for Product entity.
 * Extending JpaRepository provides basic CRUD operations, while JpaSpecificationExecutor enables dynamic queries.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Retrieves Set of Product entities by author identifier.
     *
     * @param authorId Unique author (User) identifier (ID).
     * @return Set of Product entities.
     */
    Set<Product> findByAuthorId(Long authorId);

    /**
     * Retrieves Product by identifier, eagerly fetching associated Set of SellerOffer entities.
     *
     * @param id Unique product identifier (ID).
     * @return Optional containing Product entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"sellerOfferSet"})
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithSellerOffers(Long id);

    /**
     * Retrieves Product by identifier, eagerly fetching associated author (User) entity along with his associated Product entities.
     *
     * @param id Unique product identifier (ID).
     * @return Optional containing Product entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"author","author.productSet"})
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithAuthor(Long id);
}
