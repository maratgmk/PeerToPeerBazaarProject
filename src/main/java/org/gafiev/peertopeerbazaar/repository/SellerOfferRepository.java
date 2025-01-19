package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * интерфейс для работы с БД методами CRUD
 * и созданными кастомными методами
 */
@Repository
public interface SellerOfferRepository extends JpaRepository<SellerOffer, Long>, JpaSpecificationExecutor<SellerOffer> {
    /**
     * получение из БД оффера продавца по его Id с подтягиванием всех выбранных уже частей заказа покупателя,
     * подтягивание ленивой части
     * @param id идентификатор оффера
     * @return Optional оффера
     */
    @EntityGraph(attributePaths = {"partOfferToBuySet"})
    @Query("SELECT o FROM SellerOffer o  WHERE o.id = :id")
    Optional<SellerOffer> findByIdWithPartOfferToBuy(Long id);
}
