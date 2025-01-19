package org.gafiev.peertopeerbazaar.service;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.PaymentCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.BuyerOrderMapper;
import org.gafiev.peertopeerbazaar.mapper.PaymentMapper;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.PaymentRepository;
import org.gafiev.peertopeerbazaar.repository.specification.PaymentSpecification;
import org.gafiev.peertopeerbazaar.service.interfaces.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final PaymentMapper paymentMapper;
    private final BuyerOrderMapper buyerOrderMapper;

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
     * получение DTO платежа из БД по его Id вместе с его ленивой частью
     *
     * @param id идентификатор платежа
     * @return DTO платежа вместе с множеством заказов, оплаченных этим платежом
     */
    @Override
    public PaymentResponse getPaymentByIdWithBuyerOrder(Long id) {
        Payment payment = paymentRepository.findByIdWithBuyerOrder(id)
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
     * создание нового DTO платежа по известным параметрам, переданных покупателем
     *
     * @param paymentCreate данные присланные клиентом для создания нового платежа
     * @return DTO платежа
     */
    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest paymentCreate) {

        Set<BuyerOrder> buyerOrderSet = paymentCreate.buyerOrderIds().stream()
                .map(buyerOrderId -> buyerOrderRepository.findById(buyerOrderId)
                        .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId)))))
                .collect(Collectors.toSet());

        Payment payment = new Payment();
        payment.setAmount(paymentCreate.amount());
        payment.setPaymentMode(paymentCreate.paymentMode());
        payment.setPaymentStatus(paymentCreate.paymentStatus());
        payment.setBuyerOrderSet(buyerOrderSet);

        paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment);
    }

    /**
     * исправление или замена данных в уже существующем платеже на информацию,
     * которую передаёт клиент в запросе
     *
     * @param id         идентификатор существующего платежа
     * @param paymentNew информация, переданная клиентом, которую надо внести для исправления существующего платежа
     * @return DTO платежа
     */
    @Override
    @Transactional
    public PaymentResponse updatePayment(Long id, PaymentCreateRequest paymentNew) {
        Payment payment = paymentRepository.findByIdWithBuyerOrder(id)
                .orElseThrow(() -> new EntityNotFoundException(Payment.class, Map.of("id", String.valueOf(id))));

        Set<BuyerOrder> buyerOrderSet = paymentNew.buyerOrderIds().stream()
                .map(buyerOrderId -> buyerOrderRepository.findById(buyerOrderId)
                        .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId)))))
                .collect(Collectors.toSet());

        payment.setAmount(paymentNew.amount());
        payment.setPaymentMode(paymentNew.paymentMode());
        payment.setPaymentStatus(paymentNew.paymentStatus());
        payment.setBuyerOrderSet(buyerOrderSet);

        paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment);
    }

    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
