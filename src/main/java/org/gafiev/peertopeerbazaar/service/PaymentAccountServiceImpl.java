package org.gafiev.peertopeerbazaar.service;


import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.PaymentAccountFilterRequest;
import org.gafiev.peertopeerbazaar.dto.request.PaymentAccountRequest;
import org.gafiev.peertopeerbazaar.dto.response.PaymentAccountResponse;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentAccount;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.PaymentAccountMapper;
import org.gafiev.peertopeerbazaar.repository.PaymentAccountRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.PaymentAccountSpecification;
import org.gafiev.peertopeerbazaar.service.interfaces.PaymentAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@AllArgsConstructor
public class PaymentAccountServiceImpl implements PaymentAccountService {

    private final PaymentAccountRepository paymentAccountRepository;
    private final UserRepository userRepository;
    private final PaymentAccountMapper paymentAccountMapper;

    /**
     * получает DTO платежный аккаунт из БД по его Id
     *
     * @param id идентификатор платёжного аккаунта
     * @return DTO payment account
     */
    @Override
    public PaymentAccountResponse getPaymentAccountById(Long id) {
        PaymentAccount paymentAccount = paymentAccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PaymentAccount.class, Map.of("id", String.valueOf(id))));
        return paymentAccountMapper.toPaymentAccountResponse(paymentAccount);
    }

    /**
     * находит множество всех DTO платежных аккаунтов согласно требуемых параметров и условий поиска
     *
     * @param filterRequest фильтр, который содержит заданные параметры и условия
     * @return множество всех DTO payment account
     */
    @Override
    public Set<PaymentAccountResponse> getAllPaymentAccounts(PaymentAccountFilterRequest filterRequest) {
        List<PaymentAccount> paymentAccountList = paymentAccountRepository.findAll(PaymentAccountSpecification.filterByParams(filterRequest));
        return paymentAccountMapper.toPaymentAccountResponseSet(new HashSet<>(paymentAccountList));
    }

    /**
     * создание нового платёжного аккаунта
     * @param paymentCreateRequest информация от клиента необходимая для создания нового платёжного аккаунта
     * @return DTO платёжного аккаунта
     */
    @Override
    @Transactional
    public PaymentAccountResponse createPaymentAccount(PaymentAccountRequest paymentCreateRequest) {
        User user = userRepository.findByEmail(paymentCreateRequest.email())
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", paymentCreateRequest.email())));

        PaymentAccount paymentAccount = new PaymentAccount();
        paymentAccount.setAccountNumber(paymentCreateRequest.accountNumber());
        paymentAccount.setCurrency(paymentCreateRequest.currency());
        paymentAccount.setBalance(paymentCreateRequest.balance());
        paymentAccount.setPaymentMode(paymentCreateRequest.paymentMode());
        paymentAccount.setPaymentStatus(paymentCreateRequest.paymentStatus());
        paymentAccount.setBankCode(paymentCreateRequest.bankCode());
        paymentAccount.setAccountLimit(paymentCreateRequest.accountLimit());
        paymentAccount.setIsVerified(paymentCreateRequest.isVerified());
        paymentAccount.setUser(user);

        paymentAccountRepository.save(paymentAccount);

        return paymentAccountMapper.toPaymentAccountResponse(paymentAccount);
    }

    /**
     * обновление существующего аккаунта
     * @param id идентификатор существующего платёжного аккаунта
     * @param paymentAccountNew информация от клиента, которую нужно добавить в существующий аккаунт
     * @return DTO платёжного аккаунта
     */
    @Transactional
    @Override
    public PaymentAccountResponse updatePaymentAccount(Long id, PaymentAccountRequest paymentAccountNew) {
        PaymentAccount paymentAccount = paymentAccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PaymentAccount.class, Map.of("id", String.valueOf(id))));

        User user = userRepository.findByEmail(paymentAccountNew.email())
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", paymentAccountNew.email())));

        paymentAccount.setAccountNumber(paymentAccountNew.accountNumber());
        paymentAccount.setCurrency(paymentAccountNew.currency());
        paymentAccount.setBalance(paymentAccountNew.balance());
        paymentAccount.setPaymentMode(paymentAccountNew.paymentMode());
        paymentAccount.setPaymentStatus(paymentAccountNew.paymentStatus());
        paymentAccount.setBankCode(paymentAccountNew.bankCode());
        paymentAccount.setAccountLimit(paymentAccountNew.accountLimit());
        paymentAccount.setIsVerified(paymentAccountNew.isVerified());
        paymentAccount.setUser(user);

        paymentAccountRepository.save(paymentAccount);

        return paymentAccountMapper.toPaymentAccountResponse(paymentAccount);
    }

    /**
     * удаление платёжного аккаунта из БД по Id
     * @param id идентификатор платёжного аккаунта
     */
    @Override
    @Transactional
    public void deletePaymentAccount(Long id) {
        paymentAccountRepository.deleteById(id);
    }
}