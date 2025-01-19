package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryCreateRequest(

        @Nullable @Future(message = "Expected time of delivery must be later than indicated here")
        LocalDateTime expectedDateTime,

        @Nonnull @Positive(message = "Id of buyer order must be positive number")
        Long buyerOrderId,

        @Nonnull @Positive(message = "Id of address must be positive number")
        Long addressId ) {
}
