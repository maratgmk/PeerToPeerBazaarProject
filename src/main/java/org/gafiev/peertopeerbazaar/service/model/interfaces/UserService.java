package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.UserUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.UserResponse;

import java.util.Set;

/**
 * Provides methods for User data operations.
 */
public interface UserService {

    /**
     * Retrieves a User response DTO by user's ID.
     *
     * @param id Unique User identifier (ID).
     * @return UserResponse DTO.
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves a User response DTO by user's email.
     *
     * @param email User's email.
     * @return UserResponse DTO.
     */
    UserResponse getUserByEmail(String email);

    /**
     * Retrieves a Set of UserResponse DTOs based on the specified filter criteria.
     *
     * @param filterRequest UserFilterRequest DTO containing filter criteria.
     * @return Set of UserResponse DTOs.
     */
    Set<UserResponse> getAllUsers(UserFilterRequest filterRequest);

    /**
     * Retrieves a User response DTO by user's ID, eagerly fetching the associated BuyerOrder and SellerOffer entities.
     *
     * @param id Unique User identifier (ID).
     * @return UserResponse DTO.
     */
    UserResponse getUserByIdWithBuyerOrdersAndSellerOffers(Long id);

    /**
     * Retrieves a User response DTO by user's ID, eagerly fetching the associated Basket entity.
     *
     * @param id Unique User identifier (ID).
     * @return UserResponse DTO.
     */
    UserResponse getUserByIdWithBasket(Long id);

    /**
     * Retrieves a User response DTO by user's ID, eagerly fetching the associated all entities.
     *
     * @param id Unique User identifier (ID).
     * @return UserResponse DTO.
     */
    UserResponse findByIdFull(Long id);

    /**
     * Updates an existing User entity using the information from UserUpdateRequest DTO.
     *
     * @param id          Unique User identifier (ID).
     * @param updatedUser UserUpdateRequest DTO.
     * @return UserResponse DTO.
     */
    UserResponse updateUser(Long id, UserUpdateRequest updatedUser);

    /**
     * Deletes a User entity by his ID from database.
     *
     * @param id Unique User identifier (ID).
     */
    void deleteUserById(Long id);
}
