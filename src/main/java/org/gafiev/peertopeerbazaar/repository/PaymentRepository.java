package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Payment entity.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor for dynamic queries.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    /**
     * Retrieves Payment by its identifier, eagerly fetching the associated BuyerOrder entities.
     *
     * @param id Unique Payment identifier.
     * @return Optional containing SellerOffer entity if found, otherwise empty.
     */
    @Query("SELECT p FROM Payment p JOIN FETCH p.buyerOrderSet b WHERE p.id = :id")
    Optional<Payment> findByIdWithBuyerOrders(@Param("id") Long id);
}
