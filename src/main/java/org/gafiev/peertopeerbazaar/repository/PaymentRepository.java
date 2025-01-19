package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * интерфейс предоставляет методы для получения и сохранения платежей в БД,
 * включая стандартные методы CRUD и кастомные методы.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    /**
     * Находит платеж Id вместе с подтягиванием ленивой части - множества заказов покупателя
     *
     * @param id идентификатор платежа
     * @return Optional платеж
     */
    @Query("SELECT p FROM Payment p JOIN FETCH p.buyerOrderSet b WHERE p.id = :id")
    Optional<Payment> findByIdWithBuyerOrder(@Param("id") Long id);

}
