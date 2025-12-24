package org.gafiev.peertopeerbazaar.service.model.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.SellerOfferResponse;

import java.util.Set;

/**
 * Provides methods for SellerOffer data operations.
 */
public interface SellerOfferService {

    /**
     * Retrieves seller offer by ID.
     *
     * @param id Unique seller offer identifier (ID).
     * @return SellerOfferResponse DTO.
     */
    SellerOfferResponse getSellerOfferById(Long id);

    /**
     * Retrieves a SellerOffer response DTO by its ID, eagerly fetching the associated PartOfferToBuy entities
     *
     * @param id Unique seller offer identifier (ID).
     * @return SellerOfferResponse DTO.
     */
    SellerOfferResponse getSellerOfferByIdWithPartOfferToBuy(Long id);

    /**
     * Retrieves all seller offers created by the specified seller.
     *
     * @param userId Unique seller identifier (ID).
     * @return Set of SellerOfferResponse DTOs.
     */
    Set<SellerOfferResponse> getAllMySellerOffers(Long userId);

    /**
     * Retrieves Set of SellerOffer responses based on filter criteria.
     *
     * @param filterRequest The SellerOfferFilterRequest DTO containing filter criteria data.
     * @return A Set of SellerOfferResponse DTOs.
     */
    Set<SellerOfferResponse> getAllSellerOffers(SellerOfferFilterRequest filterRequest);

    /**
     * Creates the new seller offer using data from SellerOfferCreateRequest DTO.
     *
     * @param sellerId          Unique seller identifier (ID).
     * @param sellerOfferCreate Data for creating the new seller offer.
     * @return SellerOfferResponse DTO.
     */
    SellerOfferResponse createSellerOffer(Long sellerId, SellerOfferCreateRequest sellerOfferCreate);

    /**
     * Updates an existing seller offer using new data from the SellerOfferCreateRequest DTO.
     *
     * @param sellerId       Unique seller identifier (ID).
     * @param id             Unique seller offer identifier (ID).
     * @param sellerOfferNew The new data for updating the existing seller offer.
     * @return SellerOfferResponse DTO.
     */
    SellerOfferResponse updateMySellerOffer(Long sellerId, Long id, SellerOfferCreateRequest sellerOfferNew);

    /**
     * Deletes a seller offer by its ID from database.
     *
     * @param id Unique seller offer identifier (ID).
     */
    void deleteSellerOffer(Long id);

    /**
     * Allows a user to get the real current unit count of the associated parts.
     *
     * @param id Unique seller offer identifier (ID).
     * @return The current unit count number.
     */
    Integer getActualUnitCount(@NotNull @Positive Long id);
}
