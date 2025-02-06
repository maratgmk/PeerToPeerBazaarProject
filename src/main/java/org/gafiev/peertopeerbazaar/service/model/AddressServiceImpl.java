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
    private final AddressMapper addressMapper;
    private final ExternalDroneService externalDroneService;

    /**
     * получение DTO адреса по его идентификатору
     *
     * @param id идентификатор адреса
     * @return DTO адрес
     */
    @Override
    public AddressResponse getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(id))));
        return addressMapper.toAddressResponse(address);
    }

    /**
     * получение множества адресов из БД согласно настроенного фильтра
     *
     * @param filterRequest фильтр определяющий условия и параметры поиска
     * @return DTO addressSet
     */
    @Override
    public Set<AddressResponse> getAllAddresses(AddressFilterRequest filterRequest) {
        List<Address> addressList = addressRepository.findAll(AddressSpecifications.filterByParams(filterRequest));
        Set<Address> addressSet = new HashSet<>(addressList);
        return addressMapper.toAddressResponseSet(addressSet);
    }

    /**
     * получение всех адресов, связанных с одним пользователем
     *
     * @param userId идентификатор пользователя
     * @return DTO addressSet
     */
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

    /**
     * создание нового адреса
     *
     * @param candidate информация введённая пользователем
     * @return DTO адрес
     */
    @Override
    @Transactional
    public AddressResponse createAddress(AddressCreateRequest candidate) {
        checkAddress(candidate);

        Address address = new Address();
        address.setTown(candidate.town());
        address.setStreet(candidate.street());
        address.setNumberBuilding(candidate.numberBuilding());
        address.setZipCode(candidate.zipCode());
        address.setLatitude(candidate.latitude());
        address.setLongitude(candidate.longitude());
        address.setAttitude(candidate.attitude());
        address.setAccuracy(candidate.accuracy());

        address = addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    /**
     * изменение существующего адреса
     *
     * @param id         идентификатор существующего адреса
     * @param userId     идентификатор пользователя
     * @param addressNew информация введённая пользователем
     * @return DTO адрес
     */
    @Override
    @Transactional
    public AddressResponse updateMyAddress(Long id, Long userId, AddressCreateRequest addressNew) {
        checkAddress(addressNew);

        Address address = addressRepository.findByIdWithSellerOffersAndDeliveries(id)
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(id))));

        address.setTown(addressNew.town());
        address.setStreet(addressNew.street());
        address.setNumberBuilding(addressNew.numberBuilding());
        address.setZipCode(addressNew.zipCode());
        address.setLatitude(addressNew.latitude());
        address.setLongitude(addressNew.longitude());
        address.setAttitude(addressNew.attitude());
        address.setAccuracy(addressNew.accuracy());


        address = addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    /**
     * удаление адреса по его идентификатору из БД
     *
     * @param id идентификатор адреса
     */
    @Transactional
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    /**
     * метод проверка на возможность вызова дрона по данному адресу
     * @param addressCreateRequest  запрос на DTO Address для создания или обновления адреса
     */
    private void checkAddress(AddressCreateRequest addressCreateRequest) {
        String code = externalDroneService.getCode(addressCreateRequest);
        CheckAddressResult result = CheckAddressResult.getByCode(code).orElseThrow();
        if (result != CheckAddressResult.ALLOWED)
            throw new DroneException(result.getDescription());
    }
}

