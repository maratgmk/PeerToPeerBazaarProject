package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting Delivery entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
@Slf4j
public class DeliveryMapper {
    private final BuyerOrderMapper buyerOrderMapper;
    private final AddressMapper addressMapper;
    private final TimeSlotMapper timeSlotMapper;

    /**
     * Converts Delivery entity to DeliveryResponse DTO.
     *
     * @param delivery Delivery entity.
     * @return DeliveryResponse DTO.
     */
    public DeliveryResponse toDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .deliveryStatus(delivery.getDeliveryStatus())
                .timeSlot(delivery.getTimeSlot() != null ?
                        timeSlotMapper.toTimeSlotResponse(delivery.getTimeSlot()) : null)
                .orderId(delivery.getBuyerOrder().getId())
                .addressId(delivery.getToAddress().getId())
                .build();
    }

    /**
     * Converts Set of delivery entities to Set of delivery responses.
     *
     * @param deliveries Set of delivery entities.
     * @return Set of delivery responses.
     */
    public Set<DeliveryResponse> toDeliveryResponseSet(Set<Delivery> deliveries) {
        return deliveries == null ? null : deliveries.stream()
                .map(this::toDeliveryResponse)
                .collect(Collectors.toSet());
    }

    /**
     * Converts Delivery entity to DeliveryDroneRequest DTO.
     *
     * @param delivery Delivery entity.
     * @return DeliveryDroneRequest DTO.
     */
    public DeliveryDroneRequest toDeliveryDroneRequest(Delivery delivery) {
        DeliveryDroneRequest deliveryDroneRequest = DeliveryDroneRequest.builder()
                .deliveryId(delivery.getId())
                .timeSlot(delivery.getTimeSlot() == null ? null : timeSlotMapper.toTimeSlotResponse(delivery.getTimeSlot()))
                .buyerOrder(buyerOrderMapper.toBuyerOrderDroneRequest(delivery.getBuyerOrder()))
                .toAddress(addressMapper.toAddressDroneRequest(delivery.getToAddress()))
                .fromAddress(addressMapper.toAddressDroneRequest(delivery.getFromAddress()))
                .build();
        log.debug("Mapped DeliveryDroneRequest: {}", deliveryDroneRequest);

        return deliveryDroneRequest;
    }
}
