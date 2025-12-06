package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentRedirectResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalPaymentResponse;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.exception.PaymentStatusException;
import org.gafiev.peertopeerbazaar.mapper.PaymentMapper;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.PaymentRepository;
import org.gafiev.peertopeerbazaar.repository.specification.PaymentSpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalPaymentService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final PaymentMapper paymentMapper;
    private final ExternalPaymentService externalPaymentService;

    /**
     * получение DTO платежа из БД по его Id
     *
     * @param id идентификатор платежа
     * @return DTO payment
     */
    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Payment.class, Map.of("id", String.valueOf(id))));
        return paymentMapper.toPaymentResponse(payment);
    }

    /**
     * получение DTO платежа из БД по его Id вместе
     * с его ленивой частью BuyerOrderSet.
     *
     * @param id идентификатор платежа
     * @return DTO платежа вместе с множеством заказов, оплаченных этим платежом
     */
    @Override
    public PaymentResponse getPaymentByIdWithBuyerOrders(Long id) {
        Payment payment = paymentRepository.findByIdWithBuyerOrders(id)
                .orElseThrow(() -> new EntityNotFoundException(Payment.class, Map.of("id", String.valueOf(id))));
        return paymentMapper.toPaymentResponse(payment);
    }

    /**
     * получение множества всех DTO платежей согласно условий и параметров поиска
     *
     * @param filterRequest фильтр поиска в БД
     * @return множество DTO платежей, отфильтрованных согласно параметров и условий поиска
     */
    @Override
    public Set<PaymentResponse> getAllPaymentSet(PaymentFilterRequest filterRequest) {
        List<Payment> paymentList = paymentRepository.findAll(PaymentSpecification.filterByParams(filterRequest));
        Set<Payment> paymentSet = new HashSet<>(paymentList);
        return paymentMapper.toPaymentResponseSet(paymentSet);
    }


    /**
     * исправление или замена данных в уже существующем платеже на информацию,
     * которую передаёт клиент или внешнее платежное приложение в запросе callback notify.
     *
     * @param id         идентификатор существующего платежа
     * @param paymentUpdateRequest информация, переданная клиентом, которую надо внести для исправления существующего платежа
     * @return DTO платежа
     */
    @Override
    @Transactional
    public PaymentResponse updatePayment(Long id, PaymentUpdateRequest paymentUpdateRequest) {
        // Эта проверка в начале метода для симуляции ошибки в тесте
        if (id == 0) {
            throw new RuntimeException("Simulated error for test"); // Это вызовет 500 в контроллере
        }

        Payment payment = paymentRepository.findByIdWithBuyerOrders(id)
                .orElseThrow(() -> new EntityNotFoundException(Payment.class, Map.of("id", String.valueOf(id))));

        if(paymentUpdateRequest.amount() != null){
            payment.setAmount(paymentUpdateRequest.amount());
        }

        if(paymentUpdateRequest.buyerOrderIdsToAdd() != null && !paymentUpdateRequest.buyerOrderIdsToAdd().isEmpty()){
            Set<BuyerOrder> buyerOrderSet = Objects.requireNonNullElse(paymentUpdateRequest.buyerOrderIdsToAdd(),Set.<Long>of()).stream()
                    .map(buyerOrderId -> buyerOrderRepository.findById(buyerOrderId)
                            .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId)))))
                    .collect(Collectors.toSet());
            payment.setBuyerOrderSet(buyerOrderSet);
        }

        if(paymentUpdateRequest.buyerOrderIdsToRemove() != null && !paymentUpdateRequest.buyerOrderIdsToRemove().isEmpty()){
            Set<BuyerOrder> buyerOrderSet = Objects.requireNonNullElse(paymentUpdateRequest.buyerOrderIdsToRemove(),Set.<Long>of()).stream()
                    .map(buyerOrderId -> buyerOrderRepository.findById(buyerOrderId)
                    .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class,Map.of("id", String.valueOf(buyerOrderId)))))
                    .collect(Collectors.toSet());
            payment.setBuyerOrderSet(buyerOrderSet);
        }

        if(paymentUpdateRequest.paymentMode() != null){
            payment.setPaymentMode(paymentUpdateRequest.paymentMode());
        }

        if(paymentUpdateRequest.paymentStatus() != null){
            payment.setPaymentStatus(paymentUpdateRequest.paymentStatus());
        }
        if(paymentUpdateRequest.completionDateTime() != null){
            payment.setCompletionDateTime(paymentUpdateRequest.completionDateTime());
        }

        payment = paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentRedirectResponse completePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Payment.class, Map.of("id", String.valueOf(id))));

        ExternalPaymentResponse paymentResponse = externalPaymentService.createTransaction(paymentMapper.toExternalPaymentRequest(payment));
        if (paymentResponse.error() != null) {
            String message = "Can not get payment page Uri from External Payment Service : paymentId =%s, reason = %s".formatted(id, paymentResponse.error());
            log.error(message);
            throw new PaymentStatusException(message); //Обработать это исключение в AppControllerAdvice
        }
        if (paymentResponse.status() == PaymentStatus.DENIED) {
            String message = "Unsuccessful status  : paymentId =%s, status = %s".formatted(id, paymentResponse.status());
            log.error(message);
            throw new PaymentStatusException(message);
        }
        payment.setPaymentStatus(paymentResponse.status());
        payment.setCompletionDateTime(paymentResponse.completionDateTime());
        payment = paymentRepository.save(payment);

        return PaymentRedirectResponse.builder()
                .id(payment.getId())
                .paymentPageUri(paymentResponse.paymentUri())
                .build();
    }

    @Transactional
    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
