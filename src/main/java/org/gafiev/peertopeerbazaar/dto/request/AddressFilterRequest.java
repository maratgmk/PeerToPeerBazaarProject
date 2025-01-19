package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

/**
 * поиск адресов из БД по заданным условиям и параметрам
 *
 * @param ids            набор идентификаторов адресов
 * @param town           название населённого пункта
 * @param street         название улицы
 * @param numberBuilding номер дома
 * @param longitudeRight граница долготы справа
 * @param longitudeLeft  граница долготы слева
 * @param latitudeNorth  граница по северной широте
 * @param latitudeSouth  граница по южной широте
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record AddressFilterRequest(
        @Size(min = 1) Set<Long> ids,
        @Size(min = 1) String town,
        @Size(min = 1) String street,
        @Positive Integer numberBuilding,
        @Positive Double longitudeRight,
        @Positive Double longitudeLeft,
        @Positive Double latitudeNorth,
        @Positive Double latitudeSouth,
        @Positive Double attitudeHigh,
        @Positive Double attitudeLow) {
}
