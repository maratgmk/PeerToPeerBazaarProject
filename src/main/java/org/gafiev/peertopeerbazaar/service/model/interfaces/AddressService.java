package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.AddressResponse;

import java.util.Set;

/**
 * Provides methods to work with Address data.
 */

public interface AddressService {
    /**
     * Retrieves Set of Address entities from database based on specific filter criteria.
     *
     * @param filterRequest Filter criteria for searching Address entities in database.
     * @return Set of Address response DTOs.
     */
    Set<AddressResponse> getAllAddresses(AddressFilterRequest filterRequest);

    /**
     * Retrieves all Addresses belonging to User.
     *
     * @param userId Unique identifier of User.
     * @return Set of Address response DTOs.
     */
    Set<AddressResponse> getAllMyAddresses(Long userId);

    /**
     * Retrieves Address using its identifier.
     *
     * @param id Unique identifier of Address.
     * @return Address response DTO.
     */
    AddressResponse getAddressById(Long id);

    /**
     * Creates new Address record.
     *
     * @param addressRequest Data required for Address creation.
     * @return Newly created Address response DTO.
     */
    AddressResponse createAddress(AddressCreateRequest addressRequest);

    /**
     * Updates User's Address.
     *
     * @param id Unique identifier of Address to update.
     * @param addressDetails Data containing updated Address details.
     * @return Address response DTO.
     */
    AddressResponse updateMyAddress(Long id, AddressCreateRequest addressDetails);

    /**
     * Deletes Address record from database.
     *
     * @param id Unique identifier of Address to delete.
     */
    void deleteAddress(Long id);
}

