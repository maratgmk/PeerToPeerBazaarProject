package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DeliveryMapper {
    private final BuyerOrderMapper buyerOrderMapper;
    private final AddressMapper addressMapper;
    private final TimeSlotMapper timeSlotMapper;

    public DeliveryResponse toDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .deliveryStatus(delivery.getDeliveryStatus())
                .timeSlot(delivery.getTimeSlot())
                .orderId(delivery.getBuyerOrder().getId())
                .addressId(delivery.getToAddress().getId())
                .build();
    }

    public Set<DeliveryResponse> toDeliveryResponseSet(Set<Delivery> deliveries) {
        return deliveries == null ? null : deliveries.stream()
                .map(this::toDeliveryResponse)
                .collect(Collectors.toSet());
    }

    public DeliveryDroneRequest toDeliveryDroneRequest(Delivery delivery) {
        return DeliveryDroneRequest.builder()
                .timeSlot(delivery.getTimeSlot() == null ? null : timeSlotMapper.toTimeSlotResponse(delivery.getTimeSlot()))
                .buyerOrder(buyerOrderMapper.toBuyerOrderDroneRequest(delivery.getBuyerOrder()))
                .toAddress(addressMapper.toAddressDroneRequest(delivery.getToAddress()))
                .fromAddress(addressMapper.toAddressDroneRequest(delivery.getFromAddress()))
                .build();
    }
}
