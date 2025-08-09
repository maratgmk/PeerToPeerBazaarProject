package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
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
        @NotNull @Nonnull @Size(min = 1) Set<Long> ids,
        @NotNull @Nonnull @Size(min = 1) String town,
        @NotNull @Nonnull @Size(min = 1) String street,
        @NotNull @Nonnull @Positive Integer numberBuilding,
        @NotNull @Nonnull @Positive Double longitudeRight,
        @NotNull @Nonnull @Positive Double longitudeLeft,
        @NotNull @Nonnull @Positive Double latitudeNorth,
        @NotNull @Nonnull @Positive Double latitudeSouth,
        @NotNull @Nonnull @Positive Double attitudeHigh,
        @NotNull @Nonnull @Positive Double attitudeLow) {
}
