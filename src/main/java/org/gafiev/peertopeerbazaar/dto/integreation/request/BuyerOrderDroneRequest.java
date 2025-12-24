package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO containing data to check if a specified buyer order parameters are serviceable by the drone service.
 *
 * @param weightKg  The total weight of the buyer order in kilograms.
 * @param volumeLtr The total volume of the buyer order in liters.
 */
@Schema(description = "Request containing weight and volume to verify drone serviceability for an order")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record BuyerOrderDroneRequest(
        @Schema(description = "Weight of buyer order (kg)",example = "19.57")
        BigDecimal weightKg,

        @Schema(description = "Volume of buyer order (liters)",example = "21.76")
        BigDecimal volumeLtr) {
}
