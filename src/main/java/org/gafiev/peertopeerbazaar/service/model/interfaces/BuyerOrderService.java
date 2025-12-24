package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;

/**
 * Provides methods for BuyerOrder data operations.
 */
public interface BuyerOrderService {

    /**
     * Retrieves BuyerOrder response DTO by identifier with buyer (User entity).
     *
     * @param buyerOrderId Unique BuyerOrder identifier.
     * @return BuyerOrder response DTO.
     */
    BuyerOrderResponse getByIdWithBuyer(Long buyerOrderId);

    /**
     * Retrieves BuyerOrder response DTO by identifier and buyer identifier.
     *
     * @param buyerId      Unique User identifier.
     * @param buyerOrderId Unique BuyerOrder identifier.
     * @return BuyerOrder response DTO.
     */
    BuyerOrderResponse get(Long buyerOrderId, Long buyerId);

    /**
     * Retrieves Set of BuyerOrder responses DTOs by status and buyer identifier.
     *
     * @param buyerId          Unique User identifier.
     * @param buyerOrderStatus Current buyer order status.
     * @return Set of BuyerOrder response DTOs.
     */
    Set<BuyerOrderResponse> getAllByStatus(Long buyerId, BuyerOrderStatus buyerOrderStatus);

    /**
     * Retrieves Set of BuyerOrder response DTOs by filter criteria.
     *
     * @param filterRequest BuyerOrderFilterRequest DTO with filter criteria.
     * @return Set of BuyerOrder response DTOs.
     */
    Set<BuyerOrderResponse> getAllBuyerOrders(BuyerOrderFilterRequest filterRequest);

    /**
     * Creates new BuyerOrder from BuyerOrderCreateRequest DTO.
     *
     * @param buyerId   Unique User identifier.
     * @param candidate BuyerOrderCreateRequest DTO with creation data.
     * @return Set of BuyerOrder response DTOs.
     */
    Set<BuyerOrderResponse> create(Long buyerId, BuyerOrderCreateRequest candidate);

    /**
     * Updates existing BuyerOrder from BuyerOrderUpdateRequest DTO.
     *
     * @param buyerId      Unique User identifier.
     * @param buyerOrderId Unique BuyerOrder identifier.
     * @param requestNew   BuyerOrderUpdateRequest DTO with update data.
     * @return BuyerOrder response DTO.
     */
    BuyerOrderResponse update(Long buyerId, Long buyerOrderId, BuyerOrderUpdateRequest requestNew);

    /**
     * Cancels BuyerOrder by buyer.
     *
     * @param buyerId      Unique User identifier.
     * @param buyerOrderId Unique BuyerOrder identifier.
     */
    void cancel(Long buyerId, Long buyerOrderId);

    /**
     * Deletes BuyerOrder from database.
     *
     * @param buyerOrderId Unique BuyerOrder identifier.
     */
    void delete(Long buyerOrderId);
}
