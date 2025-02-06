package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * запрос дрона из внешнего сервиса по DTO данным об адресе.
 * @param id идентификатор адреса
 * @param town населённый пункт
 * @param street улица
 * @param numberBuilding номер дома
 * @param zipCode почтовый индекс
 * @param latitude широта
 * @param longitude долгота
 * @param attitude  высота
 * @param accuracy погрешность измерения
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressDroneRequest(
        Long id,
        String town,
        String street,
        Integer numberBuilding,
        Integer zipCode,
        Double latitude,
        Double longitude,
        Double attitude,
        Double accuracy) {
}
