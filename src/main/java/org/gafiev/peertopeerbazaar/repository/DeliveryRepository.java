package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for Delivery entities.
 * Supports basic CRUD operations and dynamic query execution via JpaSpecificationExecutor.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {
    /**
     * Finds deliveries matching a specific status, origin and destination addresses, and time slot.
     *
     * @param deliveryStatus Current status of the delivery.
     * @param toAddress      The destination address.
     * @param fromAddress    The origin address.
     * @param timeSlot       The scheduled time window.
     * @return A set of matching deliveries.
     */
    @Query("SELECT d  FROM Delivery d  WHERE d.deliveryStatus = :deliveryStatus" +
            " and d.toAddress = :toAddress and d.fromAddress = :fromAddress" +
            " and d.timeSlot = :timeSlot")
    Set<Delivery> findAllByDeliveryStatusAndFromAddressAndToAddressAndTimeSlot(
            @Param("deliveryStatus") DeliveryStatus deliveryStatus,
            @Param("toAddress") Address toAddress,
            @Param("fromAddress") Address fromAddress,
            @Param("timeSlot") TimeSlot timeSlot);

    /**
     * Finds deliveries based on origin and destination addresses.
     *
     * @param toAddress   The destination address.
     * @param fromAddress The origin address.
     * @return A set of matching deliveries.
     */
    @Query("SELECT d  FROM Delivery d  WHERE  d.toAddress = :toAddress and d.fromAddress = :fromAddress")
    Set<Delivery> findAllByDeliveryAndFromAddressAndToAddress(
            @Param("toAddress") Address toAddress,
            @Param("fromAddress") Address fromAddress);

    /**
     * Retrieves a delivery by ID with toAddress and fromAddress eagerly fetched.
     *
     * @param id Delivery identifier.
     * @return Optional containing the delivery if found.
     */
    @EntityGraph(attributePaths = {"toAddress", "fromAddress"})
    @Query("SELECT d FROM Delivery d  WHERE d.id = :id")
    Optional<Delivery> findByIdWithAddresses(Long id);

    /**
     * Retrieves a delivery by ID with the associated BuyerOrder eagerly fetched.
     *
     * @param id Delivery identifier.
     * @return Optional containing the delivery if found.
     */
    @EntityGraph(attributePaths = {"buyerOrder"})
    @Query("SELECT d FROM Delivery d  WHERE d.id = :id")
    Optional<Delivery> findDeliveryByIdWithBuyerOrder(Long id);

    /**
     * Retrieves a delivery by ID with BuyerOrder, toAddress, and fromAddress eagerly fetched.
     *
     * @param id Delivery identifier.
     * @return Optional containing the delivery if found.
     */
    @EntityGraph(attributePaths = {"buyerOrder", "toAddress", "fromAddress"})
    @Query("SELECT d FROM Delivery d  WHERE d.id =:id")
    Optional<Delivery> findDeliveryByIdWithBuyerOrderWithAddresses(Long id);

    /**
     * Retrieves deliveries having a status from the provided set,
     * with Drone, BuyerOrder, and Buyer entities eagerly fetched.
     *
     * @param deliveryStatusSet A set of delivery statuses.
     * @return A set of deliveries matching the statuses.
     */
    @EntityGraph(attributePaths = {"buyerOrder", "buyerOrder.buyer", "drone"})
    @Query("SELECT d FROM Delivery d WHERE d.deliveryStatus IN :deliveryStatusSet")
    Set<Delivery> findAllByStatusesWithDroneAndBuyerOrderAndBuyer(Set<DeliveryStatus> deliveryStatusSet);

    /**
     * Retrieves a delivery by ID with the assigned Drone eagerly fetched.
     *
     * @param id Delivery identifier.
     * @return Optional containing the delivery if found.
     */
    @EntityGraph(attributePaths = {"drone"})
    @Query("SELECT d FROM Delivery d WHERE d.id =:id")
    Optional<Delivery> findDeliveryByIdWithDrone(Long id);

    /**
     * Retrieves a delivery by ID with all related entities (Buyer, Seller, Drone, Payment) eagerly fetched.
     *
     * @param id Delivery identifier.
     * @return Optional containing the delivery if found.
     */
    @EntityGraph(attributePaths = {"buyerOrder.buyer", "buyerOrder.partOfferToBuySet.sellerOffer.seller", "drone", "buyerOrder.payment"})
    @Query("SELECT d FROM Delivery d WHERE d.id = :id")
    Optional<Delivery> findByIdWithBuyerAndSellerAndDroneAndPayment(Long id);
}

