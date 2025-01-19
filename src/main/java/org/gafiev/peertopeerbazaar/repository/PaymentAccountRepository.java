package org.gafiev.peertopeerbazaar.repository;

import org.gafiev.peertopeerbazaar.entity.payment.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Репозиторий для работы с сущностями PaymentAccount.
 * Этот интерфейс предоставляет методы для выполнения операций с платёжными аккаунтами,
 * включая стандартные операции CRUD и кастомные запросы из БД.
 */
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long>, JpaSpecificationExecutor<PaymentAccount> {

}
