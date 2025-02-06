package org.gafiev.peertopeerbazaar.repository.specification;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentAccountFilterRequest;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentAccount;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * утилитарный класс определяющий метод поиска в БД согласно переданного в запросе фильтра
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentAccountSpecification {
    public static Specification<PaymentAccount> filterByParams(@Nullable PaymentAccountFilterRequest filterRequest) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest == null) return criteriaBuilder.conjunction();

            if (filterRequest.ids() != null && !filterRequest.ids().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.ids()));
            }

            if (filterRequest.userIds() != null && !filterRequest.userIds().isEmpty()) {
                predicates.add(root.get("id").in(filterRequest.userIds()));
            }

            if (filterRequest.accountNumber() != null && !filterRequest.accountNumber().isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("account_number"), "%" + filterRequest.accountNumber() + "%"));
            }

            if (filterRequest.currency() != null) {
                predicates.add(criteriaBuilder.equal(root.get("currency"), filterRequest.currency().name()));
            }

            if (filterRequest.paymentMode() != null) {
                predicates.add(criteriaBuilder.equal(root.get("payment_mode"), filterRequest.paymentMode().name()));
            }

            if (filterRequest.paymentStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("payment_status"), filterRequest.paymentStatus().name()));
            }

            if (filterRequest.bankCode() != null && !filterRequest.bankCode().isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("bank_code"), "%" + filterRequest.bankCode() + "%"));
            }

            if (filterRequest.balanceLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("balance"), filterRequest.balanceLow()));
            }

            if (filterRequest.balanceHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("balance"), filterRequest.balanceHigh()));
            }


            if (filterRequest.balanceLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("accountLimit"), filterRequest.balanceLow()));
            }

            if (filterRequest.balanceHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("accountLimit"), filterRequest.balanceHigh()));
            }

            if (filterRequest.createdDateAfter() != null && filterRequest.createdDateBefore() != null) {
                predicates.add(criteriaBuilder.between(root.get("created_account_date"), filterRequest.createdDateAfter(), filterRequest.createdDateBefore()));
            }

            if (filterRequest.updatedDateAfter() != null && filterRequest.updatedDateBefore() != null) {
                predicates.add(criteriaBuilder.between(root.get("updated_account_date"), filterRequest.updatedDateAfter(), filterRequest.updatedDateBefore()));
            }

            if (filterRequest.accountLimitLow() != null) {
                predicates.add(criteriaBuilder.ge(root.get("account_limit"), filterRequest.accountLimitLow()));
            }

            if (filterRequest.accountLimitHigh() != null) {
                predicates.add(criteriaBuilder.le(root.get("account_limit"), filterRequest.accountLimitHigh()));
            }

            if (filterRequest.isVerified() != null) {
                predicates.add(criteriaBuilder.equal(root.get("is_verified"), filterRequest.isVerified()));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };

    }
}
