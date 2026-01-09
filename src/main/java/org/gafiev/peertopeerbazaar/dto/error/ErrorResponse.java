package org.gafiev.peertopeerbazaar.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * DTO representing a structured error response for API clients.
 *
 * @param code                Internal or HTTP-based error code.
 * @param userFriendlyMessage A clear, non-technical message suitable for display to the end user.
 * @param sourceMessage       Technical details or the original exception message for debugging purposes.
 */
@Schema(description = "Standardized error response containing details about the failure")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ErrorResponse(
        @Schema(description = "HTTP or application-specific error status code", example = "404")
        int code,

        @Schema(description = "A localized, easy-to-understand message for the user",
                example = "The requested drone is currently unavailable.")
        String userFriendlyMessage,

        @Schema(description = "Technical error details or original exception message for developers",
                example = "EntityNotFoundException: Drone with id 578 not found")
        String sourceMessage) {
}
