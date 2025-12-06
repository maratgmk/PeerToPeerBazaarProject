package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
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
 * @param numbers        множество домов
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
        @NotNull @Nonnull @Size(min = 1) Set<Integer> numbers,

        @NotNull @Nonnull @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")  // Форматирует как строку с 6 знаками после запятой при JSON-обмене
        Double longitudeRight,

        @NotNull @Nonnull @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")  // Форматирует как строку с 6 знаками после запятой при JSON-обмене
        Double longitudeLeft,

        @NotNull @Nonnull @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")  // Форматирует как строку с 6 знаками после запятой при JSON-обмене
        Double latitudeNorth,

        @NotNull @Nonnull @Positive(message = "Latitude must be a positive number.")
        @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 3 digits before the decimal point and exactly 6 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.6f")  // Форматирует как строку с 6 знаками после запятой при JSON-обмене
        Double latitudeSouth,

        @NotNull @Nonnull @Positive(message = "Attitude must be a positive number.")
        @Digits(integer = 4, fraction = 2, message = "Attitude must have at most 4 digits before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double attitudeHigh,

        @NotNull @Nonnull @Positive(message = "Attitude must be a positive number.")
        @Digits(integer = 4, fraction = 2, message = "Attitude must have at most 4 digits before the decimal point and exactly 2 after.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        Double attitudeLow) {
}
//TODO поля могут быть nullable, иначе как найти продавца в тайге?