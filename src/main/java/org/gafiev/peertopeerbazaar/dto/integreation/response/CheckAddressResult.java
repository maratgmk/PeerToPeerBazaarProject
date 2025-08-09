package org.gafiev.peertopeerbazaar.dto.integreation.response;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum CheckAddressResult {
    ALLOWED("allowed", "разрешенный адрес"),
    FORBIDDEN("forbidden", "адрес находится в нелетной зоне"),
    OUT_OF_SERVICE("out_of_service", "адрес находится вне зоны обслуживания"),
    BLACK_LIST("black_list", "адрес занесен в черный список"),
    BAD_CONDITION("bad_condition", "адрес находится в зоне плохих условий полета/посадки");

    /**
     * код элемента перечисления
     */
    @Nonnull
    private final String code;

    /**
     * причина отмены создания адреса
     */
    @Nonnull
    private final String description;

    /**
     * мапа, где ключом является код элемента перечисления, а значение элемент перечисления
     */
    private static final Map<String,CheckAddressResult> codeToResult = Arrays.stream(values())
            .collect(Collectors.toMap(r -> r.code.toLowerCase(Locale.ROOT), r -> r));

    /**
     * метод ищет элемент перечисления по коду
     * @param code код элемента перечисления
     * @return  элемент перечисления
     */
    @Nonnull
    public static Optional<CheckAddressResult> getByCode(@Nullable String code){
        return Optional.ofNullable(code).map(c -> c.toLowerCase(Locale.ROOT)).map(codeToResult::get);
    }

}
