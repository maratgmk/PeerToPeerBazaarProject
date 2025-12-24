package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO contains data for creating specific filter criteria.
 *
 * @param ids Set of seller offer identifiers.
 * @param offerStatus Current seller offer status.
 * @param creationDateTimeAfter The start of the creation date range (inclusive).
 * @param creationDateTimeBefore The end of the creation date range (inclusive).
 * @param finishDateTimeAfter  The start of the finish date range (inclusive).
 * @param finishDateTimeBefore  The end of the finish date range (inclusive).
 * @param productIds  Set of product identifiers.
 * @param addressIds  Set of address identifiers.
 * @param userIds  Set of seller (User) identifiers.
 */
@Schema(description = "Data for filtering seller offers of interest.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record SellerOfferFilterRequest(
      @Schema(description = "Set of seller offer IDs.", example = "[12,15,17,19]")
        @Size(min = 1) Set<Long> ids,

      @Schema(description = "Current seller offer status.", example = "PRESALE")
        OfferStatus offerStatus,

      @Schema(description = "The start date/time limit for offer creation.", example = "2025-10-15T14:31")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        @PastOrPresent LocalDateTime creationDateTimeAfter,

      @Schema(description = "The end date/time limit for offer creation.", example = "2025-10-16T10:47")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        @PastOrPresent LocalDateTime creationDateTimeBefore,

      @Schema(description = "The start date/time limit for offer finish date.", example = "2025-10-18T18:47")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime finishDateTimeAfter,

      @Schema(description = "The end date/time limit for offer finish date.", example = "2025-10-19T08:19")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime finishDateTimeBefore,

      @Schema(description = "Set of product Ids.", example = "[1,5,9]")
        @Size(min = 1) Set<Long> productIds,

      @Schema(description = "Set of address Ids.", example = "[23,45,67]")
        @Size(min = 1) Set<Long> addressIds,

      @Schema(description = "Set of seller (User) IDs.", example = "[4,6,8]")
        @Size(min = 1) Set<Long> userIds) {
}
