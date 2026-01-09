package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO representing a time slot response from or to the external drone service.
 *
 * @param start Start time slot.
 * @param end End time slot.
 */
@Schema(description = "")
public record TimeSlotResponse(
        @Schema(description = "Start time slot", example = "2025-10-15T14:30:45")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
         LocalDateTime start,
        @Schema(description = "End time slot", example = "2025-10-15T15:30:45")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
         LocalDateTime end) {
}
