package org.gafiev.peertopeerbazaar.dto.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;

/**
 * DTO BuyerOrderFilterRequest holds data required to retrieve Set of BuyerOrder from database according to the filter criteria.
 *
 * @param ids               Set of BuyerOrder identifiers.
 * @param buyerOrderStatus  Desired BuyerOrderStatus.
 * @param buyerIds          Set of User identifiers.
 * @param paymentIds        Set of Payment identifiers.
 * @param partOfferToBuyIds Set of PartOfferToBuy identifiers.
 * @param deliveryIds       Set of Delivery identifiers.
 */
@Schema(description = "Data for the specific filter criteria to get all interested buyer orders.")
@Builder
public record BuyerOrderFilterRequest(

        @Schema(description = "Set of IDs corresponding to buyer order identifiers chosen for filter.", example = "[1,3,4,7]")
        @NotNull @Nonnull @Size(min = 1) Set<Long> ids,

        @Schema(description = "Buyer order status applied to the filter.", example = "BuyerOrderStatus.PAID")
        @Nullable BuyerOrderStatus buyerOrderStatus,

        @Schema(description = "Set of IDs corresponding to buyer identifiers chosen for filter.", example = "[1,3,5]")
        @Nullable Set<Long> buyerIds,

        @Schema(description = "Set of IDs corresponding to payment identifiers chosen for filter.", example = "[1,3,4]")
        @Nullable Set<Long> paymentIds,

        @Schema(description = "Set of IDs corresponding to part identifiers from order chosen for filter.", example = "[1,3,4,7]")
        @Nullable Set<Long> partOfferToBuyIds,

        @Schema(description = "Set of IDs corresponding to delivery identifiers chosen for filter.", example = "[4,7]")
        @Nullable Set<Long> deliveryIds) {
}