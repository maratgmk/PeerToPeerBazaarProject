package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.AddressFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.AddressResponse;

import java.util.Set;

/**
 * интерфейс по работе с DTO адрес
 * создание нового адреса, изменение существующего адреса, удаление адреса
 * получение адреса по его Id, получение всех адресов клиента по его Id,
 * получение множества адресов согласно переданного фильтра в запросе
 */

public interface AddressService {

    Set<AddressResponse> getAllAddresses(AddressFilterRequest filterRequest);

    Set<AddressResponse> getAllMyAddresses(Long userId);

    AddressResponse getAddressById(Long id);

    AddressResponse createAddress(AddressCreateRequest address);

    AddressResponse updateMyAddress(Long id, Long userId, AddressCreateRequest addressDetails);

    void deleteAddress(Long id);

}
