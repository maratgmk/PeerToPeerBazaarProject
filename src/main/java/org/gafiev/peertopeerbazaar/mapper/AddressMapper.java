package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.AddressResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.AddressDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AddressMapper {
    private final SellerOfferMapper sellerOfferMapper;
    private final DeliveryMapper deliveryMapper;


    public AddressResponse toAddressResponse(Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .town(address.getTown())
                .street(address.getStreet())
                .numberBuilding(address.getNumberBuilding())
                .zipCode(address.getZipCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .accuracy(address.getAccuracy())
                .sellerOfferResponseSet(sellerOfferMapper.toSellerOfferResponseSet(address.getSellerOfferSet()))
                .deliveryResponseSet(deliveryMapper.toDeliveryResponseSet(address.getDeliverySet()))
                .build();
    }

    public Set<AddressResponse> toAddressResponseSet( Set<Address> addresses){
        return addresses == null ? null : addresses.stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toSet());
    }

    public AddressDroneRequest toAddressDroneRequest(Address address){
        return AddressDroneRequest.builder()
                .id(address.getId())
                .town(address.getTown())
                .street(address.getStreet())
                .numberBuilding(address.getNumberBuilding())
                .zipCode(address.getZipCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .accuracy(address.getAccuracy())
                .build();
    }

}
