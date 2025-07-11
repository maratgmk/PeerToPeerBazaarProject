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

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long>, JpaSpecificationExecutor<Delivery> {
    /**
     * запрос выборок поставок из БД с одинаковыми статусами, с одинаковым адресом забора и с одинаковым адресом доставки,
     * и с одинаковым временным диапазоном доставки
     * @param deliveryStatus статус доставки
     * @param toAddress адрес получения доставки
     * @param fromAddress адрес забора доставки
     * @param timeSlot  временной диапазон доставки
     * @return подмножество из множества всех доставок, выбранное по указанным параметрам
     */
    @Query("SELECT d  FROM Delivery d  WHERE d.deliveryStatus = :deliveryStatus" +
            " and d.toAddress = :toAddress and d.fromAddress = :fromAddress" +
            " and d.timeSlot = :timeSlot")
   Set<Delivery> findAllByDeliveryStatusAndFromAddressAndToAddressAndTimeSlot(
            @Param("deliveryStatus") DeliveryStatus deliveryStatus,
            @Param("toAddress") Address toAddress,
            @Param("fromAddress") Address fromAddress,
            @Param("timeSlot") TimeSlot timeSlot);

    @Query("SELECT d  FROM Delivery d  WHERE  d.toAddress = :toAddress and d.fromAddress = :fromAddress")
    Set<Delivery> findAllByDeliveryIdAndFromAddressAndToAddress(
            @Param("toAddress") Address toAddress,
            @Param("fromAddress") Address fromAddress);

    @EntityGraph(attributePaths = { "toAddress", "fromAddress"})
    @Query("SELECT d FROM Delivery d  WHERE d.id = :id")
    Optional<Delivery> findByIdWithAddresses(Long id);

    @EntityGraph(attributePaths = { "buyerOrder" })
    @Query("SELECT d FROM Delivery d  WHERE d.id = :id")
    Optional<Delivery> findDeliveryByIdWithBuyerOrder(Long id);

 @EntityGraph(attributePaths = { "buyerOrder","toAddress","fromAddress" })
 @Query("SELECT d FROM Delivery d  WHERE d.id =:id")
 Optional<Delivery> findDeliveryByIdWithBuyerOrderWithAddresses(Long id);

    @EntityGraph(attributePaths = { "buyerOrder", "buyerOrder.buyer","drone"})
    @Query("SELECT d FROM Delivery d WHERE d.deliveryStatus IN :deliveryStatusSet")
    Set<Delivery> findAllByStatusesWithDroneAndBuyerOrderAndBuyer(Set<DeliveryStatus> deliveryStatusSet);

    @EntityGraph(attributePaths = { "drone"})
    @Query("SELECT d FROM Delivery d WHERE d.id =:id")
    Optional<Delivery> findDeliveryByIdWithDrone(Long id);

    @EntityGraph(attributePaths = { "buyerOrder.buyer", "buyerOrder.partOfferToBuySet.sellerOffer.seller" })
    @Query("SELECT d FROM Delivery d WHERE d.id = :id")
    Optional<Delivery> findByIdWithBuyerAndSeller(Long id);
}

