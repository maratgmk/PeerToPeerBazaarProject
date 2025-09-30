package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserUpdateRequest(

        @Nonnull @NotBlank @Size(min = 1, max = 49)
        String firstName,

        @Nonnull @NotBlank @Size(min = 1, max = 49)
        String lastName,

        @NotBlank @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\(?\\d{1,4}\\)?[- ]?\\d{1,4}[- ]?\\d{1,4}$")
        String phone) {
}
