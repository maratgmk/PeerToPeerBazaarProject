package org.gafiev.peertopeerbazaar.entity.payment;

import com.neovisionaries.i18n.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность PaymentAccount представляет платежный счёт (аккаунт) пользователя
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "payment_account")
public class PaymentAccount {
    /**
     * id идентификатор платёжного счёта пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * accountNumber есть номер счёта
     */
    @Column(name = "account_number")
    private String accountNumber;

    /**
     * currency название предмета платежа
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 12, columnDefinition = "varchar(12) default 'UNDEFINED'")
    private CurrencyCode currency = CurrencyCode.UNDEFINED;

    /**
     * balance состояние счёта
     */
    @Column(name = "balance")
    private BigDecimal balance;

    /**
     * paymentMode название метода платежа
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    /**
     * paymentStatus отражает состояние платежа
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    /**
     * createdAccountDate дата создания аккаунта платежа
     */
    @Column(name = "created_account_date")
    private LocalDateTime createdAccountDate;

    /**
     * updatedAccountDate дата изменения аккаунта платежа
     */
    @Column(name = "updated_account_date")
    private LocalDateTime updatedAccountDate;

    /**
     * bankCode код банка или платежной системы
     */
    @Column(name = "bank_code")
    private String bankCode;

    /**
     * limit ограничение на операции по верхней границе
     */
    @Column(name = "account_limit")
    private BigDecimal accountLimit;

    /**
     * isVerified подтверждение аккаунта клиента приложением
     */
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    /**
     *  securityToken пока не ясно что это и зачем
     */
    @Column(name = "security_token")
    private String securityToken;

    /**
     * user является, как обратной, так и родительской стороной к сущности PaymentAccount, и передаёт свой ключ
     * владеющей стороне paymentAccountSet (множеству платёжных аккаунтов пользователя),
     * атрибут FetchType.LAZY относится к сущности User и означает, что без явного вызова
     * не будет загружаться из БД при вызове сущности PaymentAccount,
     * атрибут {CascadeType.MERGE, CascadeType.PERSIST} ??????? относится к объекту User и означает, ?????
     * что при создании или обновлении платежного аккаунта, будет заменяться пользователь, если он существует, ?????
     * или создаться новый объект пользователя
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private User user;

}


//Возможные расширения:
//
//История операций (отдельная сущность, связанная с PaymentAccount).
//Логирование изменения баланса.
//Поддержка нескольких типов верификаций (например, через VerificationStatus).
