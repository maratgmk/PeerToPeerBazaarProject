package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.AddressResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.AddressDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting Address entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class AddressMapper {
    private final SellerOfferMapper sellerOfferMapper;

    /**
     * Converts Address entity to AddressResponse DTO.
     *
     * @param address Address entity to convert.
     * @return Resulting AddressResponse DTO.
     */
    public AddressResponse toAddressResponse(Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .town(address.getTown())
                .street(address.getStreet())
                .buildingNumber(address.getBuildingNumber())
                .postCode(address.getPostCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .altitude(address.getAltitude())
                .accuracy(address.getAccuracy())
                .createdAt(address.getCreatedAt())
                .build();
    }

    /**
     * Converts Set of Address entities to Set of AddressResponse DTOs.
     *
     * @param addresses The Set of Address entities to convert.
     * @return The resulting Set of AddressResponse DTOs, or null if input is null.
     */
    public Set<AddressResponse> toAddressResponseSet( Set<Address> addresses){
        return addresses == null ? null : addresses.stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toSet());
    }

    /**
     * Converts  Address entity to AddressDroneRequest DTO.
     *
     * @param address Address entity to convert.
     * @return Resulting AddressDroneRequest DTO.
     */
    public AddressDroneRequest toAddressDroneRequest(Address address){
        return AddressDroneRequest.builder()
                .id(address.getId())
                .town(address.getTown())
                .street(address.getStreet())
                .buildingNumber(address.getBuildingNumber())
                .postCode(address.getPostCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .accuracy(address.getAccuracy())
                .build();
    }
}
