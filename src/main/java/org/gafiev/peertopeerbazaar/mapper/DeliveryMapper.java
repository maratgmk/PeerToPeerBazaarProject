package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DeliveryMapper {

    public DeliveryResponse toDeliveryResponse(Delivery delivery){
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .deliveryStatus(delivery.getDeliveryStatus())
                .dateTime(delivery.getExpectedDateTime())
                .orderId(delivery.getBuyerOrder().getId())
                .addressId(delivery.getToAddress().getId())
                .build();
    }
    public Set<DeliveryResponse> toDeliveryResponseSet(Set<Delivery> deliveries){
        return deliveries == null ? null : deliveries.stream()
                .map(this::toDeliveryResponse)
                .collect(Collectors.toSet());
    }
}
