package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.request.UserCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.UserResponse;

import java.util.Set;

/**
 * Сервисный класс для управления пользователями в приложении.
 * интерфейс предоставляет методы для выполнения операций с пользователями, таких как:
 * - Получение пользователя по идентификатору или электронной почте
 * - Получение всех пользователей или пользователей по роли
 * - Создание, обновление и удаление пользователей
 * - Получение пользователей с учетом их заказов
 */

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Set<UserResponse> getAllUsers(UserFilterRequest filterRequest);

    UserResponse getUserByIdWithBuyerOrdersAndSellerOffers(Long id);

    UserResponse getUserByIdWithBasket(Long id);

    UserResponse getUserByIdWithPaymentAccounts(Long id);

    UserResponse findByIdFull(Long id);

    UserResponse createUser(UserCreateRequest candidate);

    UserResponse updateUser(Long id, UserCreateRequest updatedUser);

    void deleteUserById(Long id);



}
