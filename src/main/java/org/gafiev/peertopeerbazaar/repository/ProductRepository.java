package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Репозиторий для работы с сущностями Product.
 * Этот интерфейс предоставляет методы запросов продуктов из БД,
 * включая стандартные операции CRUD и кастомные запросы.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Находит все продукты, созданные указанным автором.
     * @param authorId идентификатор пользователя
     * @return список продуктов, созданных указанным автором
     */
    Set<Product> findByAuthorId(Long authorId);

    @EntityGraph(attributePaths = {"sellerOfferSet"})
    @Query("SELECT p FROM Product p  WHERE p.id = :id")
    Optional<Product> findByIdWithSellerOffers(Long id);
}
