package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.neovisionaries.i18n.CurrencyCode;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * запрос в платежную систему на создание депозитной транзакции.
 *
 * @param paymentId   идентификатор платежа на нашей стороне
 * @param amount      мажорная сумма платежа (с копейками)
 * @param currency    валюта
 * @param callbackUri Uri на который платежная система пришлет запрос подтверждение того, что платёж выполнен (статус успешно или статус отклонен)
 * @param returnUri   Uri на который платежная система вернет пользователя после завершения оплаты на платежной странице
 * @param merchantId   идентификатор нашего приложения (компании) на стороне платежной системы
 * @param signature   подпись (конкатенация значений всех полей, записанных в алфавитном порядке названий полей + secretKey, все это зашифровано SHA-256)
 */
@Builder(toBuilder = true)
public record ExternalPaymentRequest(
        @Nonnull @NonNull Long paymentId,
        @Nonnull @NonNull BigDecimal amount,
        @Nonnull @NonNull CurrencyCode currency,
        @Nonnull @NonNull String callbackUri,
        @Nonnull @NonNull String returnUri,
        @Nonnull @NonNull String merchantId,
        @Nonnull @NonNull String signature

) {
}
