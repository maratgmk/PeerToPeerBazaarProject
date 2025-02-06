package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.DeliverySpecification;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DeliveryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final UserRepository userRepository;
    private final DeliveryMapper deliveryMapper;


    @Override
    public DeliveryResponse getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(Long buyerOrderId) {
        BuyerOrder buyerOrder = buyerOrderRepository.findByIdWithDelivery(buyerOrderId)
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(buyerOrderId))));
        return deliveryMapper.toDeliveryResponseSet(buyerOrder.getDeliverySet());
    }

    @Override
    public Set<DeliveryResponse> getAllDeliveries(DeliveryStatus deliveryStatus, Address toAddress, Address fromAddress, LocalDateTime start, LocalDateTime end) {
        Set<Delivery> deliverySet = deliveryRepository.findAllByDeliveryStatusAndFromAddressAndToAddressAndExpectedDateTimeBetween(deliveryStatus, toAddress, fromAddress, start, end);
        return deliveryMapper.toDeliveryResponseSet(deliverySet);
    }

    @Override
    public Set<DeliveryResponse> getAllDeliveriesByFilter(DeliveryFilterRequest filterRequest) {
        List<Delivery> deliveryList = deliveryRepository.findAll(DeliverySpecification.filterByParams(filterRequest));
        Set<Delivery> deliverySet = new HashSet<>(deliveryList);
        return deliveryMapper.toDeliveryResponseSet(deliverySet);
    }

    @Override
    public DeliveryResponse createDelivery(DeliveryCreateRequest delivery) {
        BuyerOrder buyerOrder = buyerOrderRepository.findByIdWithDeliveryAndAddress(delivery.buyerOrderId())
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(delivery.buyerOrderId()))));

        Address address = buyerOrder.getDeliverySet().stream()
                .map(Delivery::getToAddress)
                .filter(a -> a.getId().equals(delivery.addressId())).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(delivery.addressId()))));

        Delivery createdDelivery = new Delivery();
        createdDelivery.setExpectedDateTime(delivery.expectedDateTime());
        createdDelivery.setDeliveryStatus(DeliveryStatus.CREATED);
        createdDelivery.setBuyerOrder(buyerOrder);
        createdDelivery.setToAddress(address);
        //  createdDelivery.setDrone();
        return deliveryMapper.toDeliveryResponse(createdDelivery);
    }

    @Override
    public DeliveryResponse createDeliveryDependsOnFail(Long failDeliveryId) {
        Delivery deliveryFail = deliveryRepository.findById(failDeliveryId)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(failDeliveryId))));

        Delivery createdDelivery = new Delivery();
        createdDelivery.setExpectedDateTime(deliveryFail.getExpectedDateTime());
        createdDelivery.setBuyerOrder(deliveryFail.getBuyerOrder());
        createdDelivery.setToAddress(deliveryFail.getToAddress());
        createdDelivery.setDeliveryStatus(DeliveryStatus.CREATED);
        //  createdDelivery.setDrone(deliveryFail.getDrone());

        return deliveryMapper.toDeliveryResponse(createdDelivery);
    }

    @Override
    public DeliveryResponse updateDeliveryDetails(Long id, DeliveryCreateRequest deliveryNew) {
        BuyerOrder buyerOrder = buyerOrderRepository.findByIdWithDeliveryAndAddress(deliveryNew.buyerOrderId())
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("id", String.valueOf(deliveryNew.buyerOrderId()))));

        Delivery delivery = buyerOrder.getDeliverySet().stream()
                .filter(d -> d.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));

        delivery.setExpectedDateTime(deliveryNew.expectedDateTime());
        delivery.setBuyerOrder(buyerOrder);
        Address addressNew = buyerOrder.getDeliverySet().stream()
                .map(Delivery::getToAddress)
                .filter(a -> a.getId().equals(deliveryNew.addressId())).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(deliveryNew.addressId()))));

        if (!delivery.getToAddress().equals(addressNew)) {
            delivery.setToAddress(addressNew);
        }
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse updateMyDeliveryDetails(Long id, Long userId, DeliveryCreateRequest deliveryNew) {
        User user = userRepository.findByIdWithBuyerOrdersAndSellerOffers(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,Map.of("id", String.valueOf(userId))));

        BuyerOrder buyerOrder = user.getBuyerOrderSet().stream()
                .filter(o -> o.getId().equals(deliveryNew.buyerOrderId()))
                .findFirst().orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class,Map.of("id", String.valueOf(deliveryNew.buyerOrderId()))));

        Delivery delivery = buyerOrder.getDeliverySet().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst().orElseThrow(() -> new EntityNotFoundException(Delivery.class,Map.of("id", String.valueOf(id))));

        delivery.setExpectedDateTime(deliveryNew.expectedDateTime());
        delivery.setBuyerOrder(buyerOrder);

        Address addressNew = buyerOrder.getDeliverySet().stream()
                .map(Delivery::getToAddress)
                .filter(a -> a.getId().equals(deliveryNew.addressId())).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("id", String.valueOf(deliveryNew.addressId()))));

        if(!delivery.getToAddress().equals(addressNew)){
            delivery.setToAddress(addressNew);
        }

        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public void deleteDelivery(Long id) {
        deliveryRepository.deleteById(id);
    }
}
