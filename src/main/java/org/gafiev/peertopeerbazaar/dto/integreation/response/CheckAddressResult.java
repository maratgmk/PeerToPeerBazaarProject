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

/**
 * Result of the address serviceability check for drone delivery.
 * Represents various states of address availability or reasons for its rejection.
 */
@RequiredArgsConstructor
@Getter
public enum CheckAddressResult {
    /**
     * The address is verified and available for drone operations.
     */
    ALLOWED("allowed", "The address is verified and reachable."),

    /**
     * The address is located within a restricted No-Fly Zone (NFZ).
     */
    FORBIDDEN("forbidden", "The address is located in a restricted No-Fly Zone."),

    /**
     * The address is outside the operational boundary of the service.
     */
    OUT_OF_SERVICE("out_of_service", "The address is outside the service area."),

    /**
     * The address or customer is restricted due to security or policy reasons.
     */
    BLACK_LIST("black_list", "The address is blacklisted."),

    /**
     * Weather, terrain, or temporary obstacles make the landing site hazardous.
     */
    BAD_CONDITION("bad_condition", "Unfavorable or hazardous flight/landing conditions at the location.");

    /**
     * Unique machine-readable code for the check result.
     */
    @Nonnull
    private final String code;

    /**
     * Human-readable description of the serviceability status.
     */
    @Nonnull
    private final String description;

    /**
     * Internal mapping of string codes to enum constants.
     */
    private static final Map<String, CheckAddressResult> CODE_TO_RESULT = Arrays.stream(values())
            .collect(Collectors.toMap(
                    r -> r.code.toLowerCase(Locale.ROOT),
                    r -> r
            ));

    /**
     * Retrieves the enum constant associated with the specified code.
     *
     * @param code The unique code (case-insensitive).
     * @return An Optional containing the matched result, or empty if no match is found or code is null.
     */
    @Nonnull
    public static Optional<CheckAddressResult> getByCode(@Nullable String code) {
        return Optional.ofNullable(code).map(c -> c.toLowerCase(Locale.ROOT)).map(CODE_TO_RESULT::get);
    }
}
