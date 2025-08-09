package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями BuyerOrder.
 * Этот интерфейс предоставляет методы для выполнения операций с заказами покупателя,
 * включая стандартные операции CRUD и кастомные запросы.
 */
@Repository
public interface BuyerOrderRepository extends JpaRepository<BuyerOrder,Long>, JpaSpecificationExecutor<BuyerOrder> {

    @EntityGraph(attributePaths = {"deliverySet"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithDelivery(Long id);

    @EntityGraph(attributePaths = {"deliverySet","deliverySet.address"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithDeliveryAndAddress(Long id);

    /**
     * получение заказа вместе с частями предложения, с предложениями продавца, с адресом продавца, с платежом заказа.
     * @param id идентификатор заказа
     * @return заказ в обёртке Option
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet","partOfferToBuySet.sellerOffer", "partOfferToBuySet.sellerOffer.address", "payment"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithPartOfferToBuyAndWithSellerOfferWithAddress(Long id);

    @EntityGraph(attributePaths = {"partOfferToBuySet","deliverySet","deliverySet.address"})
    @Query("SELECT o FROM BuyerOrder o  WHERE o.id = :id")
    Optional<BuyerOrder> findByIdWithDeliveryAndAddressAndPartOfferToBuy(Long id);
}

