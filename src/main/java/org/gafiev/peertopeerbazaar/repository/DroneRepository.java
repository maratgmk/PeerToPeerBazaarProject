package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Spring Data JPA repository for Drone entities.
 * Supports basic CRUD operations and dynamic query execution via JpaSpecificationExecutor.
 */
public interface DroneRepository extends JpaRepository<Drone, Long>, JpaSpecificationExecutor<Drone> {

    /**
     * Retrieves a drone by ID, eagerly fetching its deliveries
     * and the associated buyer order.
     *
     * @param id Drone identifier.
     * @return Optional containing the drone if found.
     */
    @EntityGraph(attributePaths = {"deliverySet", "deliverySet.buyerOrder"})
    @Query("SELECT d FROM Drone d WHERE d.id = :id")
    Optional<Drone> findByIdWithDeliveriesAndBuyerOrder(Long id);
}
