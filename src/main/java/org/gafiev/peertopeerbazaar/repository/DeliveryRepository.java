package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long>, JpaSpecificationExecutor<Delivery> {
    /**
     * запрос выборок поставок из БД с одинаковыми статусами, с одинаковым адресом забора и с одинаковым адресом доставки,
     * согласно временного диапазона между двумя разными ожидаемыми временами доставок
     * @param deliveryStatus статус доставки
     * @param toAddress адрес получения доставки
     * @param fromAddress адрес забора доставки
     * @param start начальное время временного диапазона
     * @param end конечное время временного диапазона
     * @return подмножество из множества всех доставок, выбранное по указанным параметрам
     */
    @Query("SELECT d  FROM Delivery d  WHERE d.deliveryStatus = :deliveryStatus" +
            " and d.toAddress = :toAddress and d.fromAddress = :fromAddress" +
            " and d.expectedDateTime = :start and d.expectedDateTime = :end")
   Set<Delivery> findAllByDeliveryStatusAndFromAddressAndToAddressAndExpectedDateTimeBetween(
            @Param("deliveryStatus") DeliveryStatus deliveryStatus,
            @Param("toAddress") Address toAddress,
            @Param("fromAddress") Address fromAddress,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}

