package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.AddressResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.CheckAddressResult;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.DroneException;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.AddressMapper;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.AddressSpecifications;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.AddressService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final @Lazy AddressMapper addressMapper;
    private final ExternalDroneService externalDroneService;

    @Override
    public AddressResponse getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(id))));
        return addressMapper.toAddressResponse(address);
    }

    @Override
    public Set<AddressResponse> getAllAddresses(AddressFilterRequest filterRequest) {
        List<Address> addressList = addressRepository.findAll(AddressSpecifications.filterByParams(filterRequest));
        Set<Address> addressSet = new HashSet<>(addressList);
        return addressMapper.toAddressResponseSet(addressSet);
    }

    @Override
    public Set<AddressResponse> getAllMyAddresses(Long userId) {
        User user = userRepository.findByIdWithBuyerOrdersAndSellerOffers(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("userId", String.valueOf(userId))));
        Set<Address> toAddressSet = user.getBuyerOrderSet().stream()
                .flatMap(buyerOrder -> buyerOrder.getDeliverySet().stream())
                .map(Delivery::getToAddress)
                .collect(Collectors.toSet());

        Set<Address> fromAddressSet = user.getSellerOfferSet().stream()
                .map(SellerOffer::getAddress)
                .collect(Collectors.toSet());

        Set<Address> myAddressSet = new HashSet<>(toAddressSet);
        myAddressSet.addAll(fromAddressSet);

        return addressMapper.toAddressResponseSet(myAddressSet);
    }

    @Override
    @Transactional
    public AddressResponse createAddress(AddressCreateRequest createRequest) {
//        checkAddress(createRequest);

        Address address = new Address();
        address.setTown(createRequest.town());
        address.setStreet(createRequest.street());
        address.setBuildingNumber(createRequest.buildingNumber());
        address.setPostCode(createRequest.postCode());
        address.setLatitude(createRequest.latitude());
        address.setLongitude(createRequest.longitude());
        address.setAltitude(createRequest.altitude());
        address.setAccuracy(createRequest.accuracy());
//        address.setCreatedAt(createRequest.createdAt());

        address = addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateMyAddress(Long id, AddressCreateRequest addressNew) {
        checkAddress(addressNew);

        Address address = addressRepository.findByIdWithSellerOffersAndDeliveries(id)
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(id))));

        address.setTown(addressNew.town());
        address.setStreet(addressNew.street());
        address.setBuildingNumber(addressNew.buildingNumber());
        address.setPostCode(addressNew.postCode());
        address.setLatitude(addressNew.latitude());
        address.setLongitude(addressNew.longitude());
        address.setAltitude(addressNew.altitude());
        address.setAccuracy(addressNew.accuracy());

        address = addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    /**
     * Validates allowed address using external drone service API call.
     *
     * @param addressCreateRequest Address creation request DTO containing Address details.
     * @throws DroneException if validation fails or service call encounters an error.
     */
    private void checkAddress(AddressCreateRequest addressCreateRequest) {
        try {
            String code = externalDroneService.getCode(addressCreateRequest);
            CheckAddressResult result = CheckAddressResult.getByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
            if (result != CheckAddressResult.ALLOWED) {
                throw new DroneException(result.getDescription());
            }
        } catch (Exception e) {
            throw new DroneException("Failed to check address: " + e.getMessage());
        }
    }
}

