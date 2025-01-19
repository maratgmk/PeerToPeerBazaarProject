package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями Address.
 * Этот интерфейс предоставляет методы для выполнения операций с адресами пользователя,
 * включая стандартные операции CRUD и кастомные запросы.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {

    /**
     * метод поиска адреса по его идентификатору с подтягиванием набора доставок и набора предложений продавца
     * @param id идентификатор адреса
     * @return Optional адрес
     */
    @EntityGraph(attributePaths = {"sellerOfferSet", "deliverySet"})
    @Query("SELECT a FROM Address a  WHERE a.id = :id")
    Optional<Address> findByIdWithSellerOffersAndDeliveries(Long id);

}
// TODO над каждым методом поставить @Transaction и в @Service