package org.gafiev.peertopeerbazaar.repository;

import jakarta.validation.constraints.Email;
import lombok.NonNull;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Репозиторий для работы с сущностью User
 * интерфейс предоставляет методы для выполнения операций CRUD (создание, чтение, обновление, удаление)
 * с пользователями в базе данных. Он наследует стандартные методы из JpaRepository,
 * что позволяет легко управлять сущностями User без необходимости написания
 * дополнительного кода для реализации этих операций.
 * Дополнительно, можно добавлять свои собственные методы для выполнения специфических запросов,
 * таких как поиск пользователя по электронной почте или по рейтингу.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    /**
     * Находит пользователя по его идентификатору и загружает связанные сущности.
     * Этот метод использует аннотацию EntityGraph для загрузки связанных
     * сущностей, таких как продукты, заказы продавца, заказы покупателя и платежные
     * аккаунты, что позволяет избежать проблемы N+1 при доступе к связанным данным.
     * @param id идентификатор пользователя, которого нужно найти.
     * @return {@link Optional<User>} объект, содержащий найденного пользователя с его
     * связанными сущностями, если он существует, или пустой объект {@link Optional},
     * если пользователь не найден.
     */
    @EntityGraph(attributePaths = {"products", "sellerOrders", "buyerOrders", "paymentAccounts"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdFull(Long id);


    /**
     * Находит пользователя по его идентификатору и загружает связанные продукты.
     *
     * <p>
     * Этот метод использует JPQL для выполнения запроса, который выбирает пользователя
     * с указанным идентификатором и загружает его связанные продукты с помощью
     * операции JOIN FETCH.
     * </p>
     *
     * @param id идентификатор пользователя, которого нужно найти
     * @return {@link Optional<User>} объект, содержащий найденного пользователя с его продуктами,
     * если он существует, или пустой объект {@link Optional}, если пользователь не найден
     */
    @Query("SELECT u FROM User u JOIN FETCH u.productSet WHERE u.id = :id")
    Optional<User> findByIdWithProducts(@Param("id") Long id); //TODO обновить методы



    @EntityGraph(attributePaths = {"buyerOrderSet","sellerOfferSet"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdWithBuyerOrdersAndSellerOffers(@Param("id") Long id);

    /**
     * Получает сущность User по её ID вместе с ассоциированными платежными аккаунтами.
     * Этот метод использует аннотацию @EntityGraph для жадной загрузки
     * коллекции paymentAccounts, что позволяет избежать проблемы N+1
     * при извлечении данных.
     *
     * @param id ID пользователя, которого нужно получить; не должен быть null
     * @return Optional, содержащий пользователя, если он найден, или пустой Optional,
     * если пользователь с указанным ID не существует
     */
    @EntityGraph(attributePaths = {"paymentAccounts"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdWithPaymentAccounts(Long id); //TODO обновить методы

    /**
     * метод использует аннотацию EntityGraph
     * метод получения покупателя вместе с корзиной, и вместе со множеством частей оффера,
     * и вместе с самим оффером, и вместе с адресом, откуда надо забрать оффер
     * @param id ID пользователя, которого нужно получить
     * @return Optional, содержащий пользователя, если он найден, или пустой Optional,
     * если пользователь с указанным ID не существует
     */
    @EntityGraph(attributePaths = {"basket","basket.partOfferToBuySet","basket.partOfferToBuySet.sellerOffer", "basket.partOfferToBuySet.sellerOffer.address"})
    @Query("SELECT u FROM User u  WHERE u.id = :id")
    Optional<User> findByIdWithBasket(Long id);

}



