package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями BuyerOrder.
 * Этот интерфейс предоставляет методы для выполнения операций с заказами покупателя,
 * включая стандартные операции CRUD и кастомные запросы.
 */
@Repository
public interface BuyerOrderRepository extends JpaRepository<BuyerOrder,Long> {
}

