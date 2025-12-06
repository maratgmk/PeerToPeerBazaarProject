package org.gafiev.peertopeerbazaar.common;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.BasketRepository;
import org.gafiev.peertopeerbazaar.repository.BuyerOrderRepository;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.DroneRepository;
import org.gafiev.peertopeerbazaar.repository.PaymentRepository;
import org.gafiev.peertopeerbazaar.repository.ProductRepository;
import org.gafiev.peertopeerbazaar.repository.SellerOfferRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.springframework.boot.test.context.TestComponent;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@AllArgsConstructor
@TestComponent
public class TestRepositoryHelper {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final SellerOfferRepository sellerOfferRepository;
    private final BasketRepository basketRepository;
    private final BuyerOrderRepository buyerOrderRepository;
    private final PaymentRepository paymentRepository;
    private final DroneRepository droneRepository;
    private final DeliveryRepository deliveryRepository;


    public void flush() {
        userRepository.flush();
        addressRepository.flush();
        productRepository.flush();
        sellerOfferRepository.flush();
        basketRepository.flush();
        buyerOrderRepository.flush();
        paymentRepository.flush();
        droneRepository.flush();
    }

    public Address saveAddress(Address address) {
        Address saved = addressRepository.save(address);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        return saved;
    }
    public Optional<Address> findAddressById(Long id){
       return addressRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        Product saved = productRepository.save(product);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        return saved;
    }

    public User saveUser(User user) {
        User saved = userRepository.save(user);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        return saved;
    }

    public SellerOffer saveSellerOffer(SellerOffer sellerOffer) {
        SellerOffer saved = sellerOfferRepository.save(sellerOffer);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        return saved;
    }

    public User findByIdWithBasket(Long userId) {
        return userRepository.findByIdWithBasket(userId).orElseThrow();
    }

    public Basket saveBasket(Basket basket) {
        Basket saved = basketRepository.save(basket);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        return saved;
    }

    public Basket findBasketWithPartsById(long id) {
        return basketRepository.findByIdWithPartOfferToBuy(id).orElseThrow();
    }

    public SellerOffer findByIdWithPartOfferToBuy(Long sellerOfferId) {
        return sellerOfferRepository.findByIdWithPartOfferToBuy(sellerOfferId).orElseThrow();
    }

    public BuyerOrder saveBuyerOrder(BuyerOrder buyerOrder) {
        return buyerOrderRepository.save(buyerOrder);
    }

    public Optional<BuyerOrder> findByIdWithBuyer(Long buyerOrderId) {
        return buyerOrderRepository.findByIdWithBuyer(buyerOrderId);
    }

    public Optional<BuyerOrder> findById(Long buyerOrderId) {
        return buyerOrderRepository.findById(buyerOrderId);
    }

    public Long count() {
        return buyerOrderRepository.count();
    }

    public Payment savePayment(Payment payment){
        return paymentRepository.save(payment);
    }

    public Drone saveDrone(Drone drone){
        return droneRepository.save(drone);
    }

    public Delivery saveDelivery(Delivery delivery){
        return deliveryRepository.save(delivery);
    }

    public Optional<Delivery> findDeliveryById(Long id){
        return deliveryRepository.findById(id);
    }
}

