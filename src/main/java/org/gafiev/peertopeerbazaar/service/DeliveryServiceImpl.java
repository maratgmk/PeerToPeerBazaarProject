package org.gafiev.peertopeerbazaar.service;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.service.interfaces.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final AddressRepository addressRepository;
    private final DeliveryMapper deliveryMapper;


    @Override
    public DeliveryResponse getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(Long buyerOrderId) {
        BuyerOrder buyerOrder = buyerOrderRepository.findById(buyerOrderId)
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class, Map.of("buyerOrderId", String.valueOf(buyerOrderId))));
        return deliveryMapper.toDeliveryResponseSet(buyerOrder.getDeliverySet());
    }

    @Override
    public Set<DeliveryResponse> getAllDeliveries() {
        List<Delivery> deliveryList = deliveryRepository.findAll();
        Set<Delivery> deliverySet = new HashSet<>(deliveryList);
        return deliveryMapper.toDeliveryResponseSet(deliverySet);
    }

    /**
     * метод создания доставки в случае изменения адреса и заказа?
     * @param delivery
     * @return
     */
    @Override
    public DeliveryResponse createDelivery(DeliveryCreateRequest delivery) {
       BuyerOrder buyerOrder = buyerOrderRepository.findById(delivery.buyerOrderId())
               .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class,Map.of("delivery.buyerOrderId()", String.valueOf(delivery.buyerOrderId()))));

        Address address = addressRepository.findById(delivery.addressId())
                .orElseThrow(() -> new EntityNotFoundException(Address.class, Map.of("delivery.addressId()", String.valueOf(delivery.addressId()))));


        Delivery createdDelivery = new Delivery();
        createdDelivery.setExpectedDateTime(delivery.expectedDateTime());
        createdDelivery.setDeliveryStatus(DeliveryStatus.CREATED);
        createdDelivery.setBuyerOrder(buyerOrder);
        createdDelivery.setToAddress(address);
      //  createdDelivery.setDrone();
        return deliveryMapper.toDeliveryResponse(createdDelivery);
    }

    /**
     * метод создания доставки в случае неудачи
     * @param failDeliveryId
     * @return
     */
    @Override
    public DeliveryResponse createDeliveryDependsOnFail(Long failDeliveryId) {
        Delivery deliveryFail = deliveryRepository.findById(failDeliveryId)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("failDeliveryId", String.valueOf(failDeliveryId))));

        Delivery createdDelivery = new Delivery();
        createdDelivery.setExpectedDateTime(deliveryFail.getExpectedDateTime());
        createdDelivery.setBuyerOrder(deliveryFail.getBuyerOrder());
        createdDelivery.setToAddress(deliveryFail.getToAddress());
      //  createdDelivery.setDrone(deliveryFail.getDrone());
        createdDelivery.setDeliveryStatus(DeliveryStatus.CREATED);

        return deliveryMapper.toDeliveryResponse(createdDelivery);
    }


    @Override
    public DeliveryResponse updateDeliveryDetails(Long id, DeliveryCreateRequest deliveryNew) {
        BuyerOrder buyerOrder = buyerOrderRepository.findById(deliveryNew.buyerOrderId())
                .orElseThrow(() -> new EntityNotFoundException(BuyerOrder.class,Map.of("deliveryNew.buyerOrderId()", String.valueOf(deliveryNew.buyerOrderId()))));

        Address address = addressRepository.findById(deliveryNew.addressId())
                .orElseThrow(() -> new EntityNotFoundException(Address.class,Map.of("deliveryNew.addressId()", String.valueOf(deliveryNew.addressId()))));

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Delivery.class, Map.of("id", String.valueOf(id))));

     delivery.setExpectedDateTime(deliveryNew.expectedDateTime());
     delivery.setBuyerOrder(buyerOrder);
     delivery.setToAddress(address);
        return deliveryMapper.toDeliveryResponse(delivery);
    }

    @Override
    public DeliveryResponse updateMyDeliveryDetails(Long id, Long userId, DeliveryCreateRequest deliveryNew) {
        return null;
    }

    @Override
    public void deleteDelivery(Long id) {
        deliveryRepository.deleteById(id);
    }
}
