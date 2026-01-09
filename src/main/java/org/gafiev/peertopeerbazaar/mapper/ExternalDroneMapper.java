package org.gafiev.peertopeerbazaar.mapper;

import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryUpdateTime;
import org.gafiev.peertopeerbazaar.dto.integreation.request.BuyerOrderDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.stereotype.Component;

/**
 * Mapper for constructing DeliveryDroneRequest DTOs from various domain entities and requests.
 */
@Component
@RequiredArgsConstructor
public class ExternalDroneMapper {
    private final AddressMapper addressMapper;

    /**
     * Converts delivery update data and related entities into a DeliveryDroneRequest DTO.
     *
     * @param updateRequest DTO containing the requested time slot.
     * @param toAddress     Entity representing the destination address.
     * @param fromAddress   Entity representing the pickup location.
     * @param buyerOrder    Entity containing order specifications (weight, volume).
     * @return A fully populated DeliveryDroneRequest DTO.
     */
    public DeliveryDroneRequest toDeliveryDroneRequest(DeliveryUpdateTime updateRequest, Address toAddress, Address fromAddress, BuyerOrder buyerOrder) {
        return DeliveryDroneRequest.builder()
                .timeSlot(updateRequest.timeSlot())
                .toAddress(addressMapper.toAddressDroneRequest(toAddress))
                .fromAddress(addressMapper.toAddressDroneRequest(fromAddress))
                .buyerOrder(BuyerOrderDroneRequest.builder()
                        .weightKg(buyerOrder.getWeightKg())
                        .volumeLtr(buyerOrder.getVolumeLtr())
                        .build())
                .build();
    }
}
