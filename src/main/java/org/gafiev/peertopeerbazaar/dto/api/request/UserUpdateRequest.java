package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO UserUpdateRequest holds data required for updating a user.
 *
 * @param firstName User's first name.
 * @param lastName User's last name.
 * @param phone User's phone.
 */
@Schema(description = "Data for updating the user.")
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserUpdateRequest(

        @Schema(description = "User's first name.", example = "Ivan.")
        @Nonnull @NotBlank @Size(min = 1, max = 49)
        String firstName,

        @Schema(description = "User's last name.", example = "Petrov.")
        @Nonnull @NotBlank @Size(min = 1, max = 49)
        String lastName,

        @Schema(description = "User's phone.", example = "+7-812-395-12-13")
        @NotBlank @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\(?\\d{1,4}\\)?[- ]?\\d{1,4}[- ]?\\d{1,4}$")
        String phone) {
}
