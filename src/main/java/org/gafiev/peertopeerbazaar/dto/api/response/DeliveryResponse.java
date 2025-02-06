package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryResponse(
        Long id,
        DeliveryStatus deliveryStatus,
        LocalDateTime dateTime,
        Long orderId,
        Long addressId,
        Set<Long> droneIds) {
}
