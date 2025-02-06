package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.BuyerOrderDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExternalDroneMapper {
    private final AddressMapper addressMapper;

    public DeliveryDroneRequest toDeliveryDroneRequest(DeliveryCreateRequest deliveryCreateRequest, Address toAddress, Address fromAddress, BuyerOrder buyerOrder){
        return DeliveryDroneRequest.builder()
                .dateTime(deliveryCreateRequest.expectedDateTime())
                .toAddress(addressMapper.toAddressDroneRequest(toAddress))
                .fromAddress(addressMapper.toAddressDroneRequest(fromAddress))
                .buyerOrder(BuyerOrderDroneRequest.builder()
                        .weightKg(buyerOrder.getWeightKg())
                        .volumeLtr(buyerOrder.getVolumeLtr())
                        .build())
                .build();
    }

}
