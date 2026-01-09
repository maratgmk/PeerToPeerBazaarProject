package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Address entity.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor for dynamic queries.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {

    /**
     * Retrieves Address entity by its identifier, eagerly fetching associated Set of SellerOffer and Set of Delivery.
     *
     * @param id Address identifier.
     * @return Optional containing Address entity if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"sellerOfferSet", "deliverySet"})
    @Query("SELECT a FROM Address a  WHERE a.id = :id")
    Optional<Address> findByIdWithSellerOffersAndDeliveries(Long id);
}
