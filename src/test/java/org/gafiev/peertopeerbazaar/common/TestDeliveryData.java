package org.gafiev.peertopeerbazaar.common;

import com.github.javafaker.Faker;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuyStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.gafiev.peertopeerbazaar.entity.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDeliveryData {
    private static final Faker faker = new Faker();
    private static TestRepositoryHelper testRepositoryHelper;

    public static Delivery getDeliverySample(){
        return Delivery.builder()
                .buyerOrder(TestDataBuyerOrder.getBuyerOrderSample())
                .deliveryStatus(DeliveryStatus.values()[ThreadLocalRandom.current().nextInt(7)])
                .timeSlot(null)
                .fromAddress(TestDataAddress.getAddressSample())
                .toAddress(TestDataAddress.getAddressSample())
                .build();
    }

    public static TimeSlot getTimeSlotSample(){
        return new TimeSlot(LocalDateTime.of(2025,11,20,10,30,0)
                ,LocalDateTime.of(2025,11,20,12,45,0));
    }


    public static Delivery createDelivery(){
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        buyerOrder.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);
        assertNotNull(buyerOrder.getId());
        Address fromAddress = buyerOrder.getPartOfferToBuySet().stream().findFirst().orElseThrow().getSellerOffer().getAddress();
        Address toAddress = TestDataAddress.getAddressSample();
        toAddress = testRepositoryHelper.saveAddress(toAddress);
        assertNotNull(toAddress.getId());
        Delivery delivery = Delivery.builder()
                .timeSlot(getTimeSlotSample())
                .deliveryStatus(DeliveryStatus.CREATED)
                .buyerOrder(buyerOrder)
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .createdAt(Instant.now().minusSeconds(600))
                .build();
        delivery = testRepositoryHelper.saveDelivery(delivery);
        assertNotNull(delivery.getId());

        return delivery;
    }

    private static PreparedData getPreparedData() {
        Address addressSample = TestDataAddress.getAddressSample();
        addressSample = testRepositoryHelper.saveAddress(addressSample);
        assertNotNull(addressSample);
        assertNotNull(addressSample.getId());

        Product productSample = TestDataProduct.getProductSample();
        productSample = testRepositoryHelper.saveProduct(productSample);
        assertNotNull(productSample);
        assertNotNull(productSample.getId());

        User author = productSample.getAuthor();
        assertNotNull(author);
        assertNotNull(author.getId());

//        OfferStatus offerStatus = Arrays.stream(OfferStatus.values()).skip(ThreadLocalRandom.current()
//                .nextInt(0, 3)).findFirst().orElseThrow();
        OfferStatus offerStatus = OfferStatus.OPENED;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationDT;
        if (offerStatus == OfferStatus.PRESALE) {
            // Для PRESALE, creationDT должен быть в будущем
            creationDT = now.plusDays(ThreadLocalRandom.current().nextInt(1, 5))  // В будущем
                    .withHour(ThreadLocalRandom.current().nextInt(0, 23))
                    .withMinute(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);

        } else {  // Для OPENED или других
            creationDT = now.minusDays(ThreadLocalRandom.current().nextInt(0, 5))
                    .minusHours(ThreadLocalRandom.current().nextInt(0, 23))
                    .minusMinutes(ThreadLocalRandom.current().nextInt(0, 59))
                    .withSecond(0);
        }

        LocalDateTime finishDT = creationDT.plusDays(ThreadLocalRandom.current().nextInt(6, 10))
                .withHour(ThreadLocalRandom.current().nextInt(1, 23))
                .withMinute(ThreadLocalRandom.current().nextInt(1, 59))
                .withSecond(0);
        return new PreparedData(addressSample, productSample, author, offerStatus, creationDT, finishDT);

    }

    private record PreparedData(Address addressSample, Product productSample, User author, OfferStatus offerStatus,
                                  LocalDateTime creationDT, LocalDateTime finishDT) {
    }


    private static SellerOffer createSellerOffer(int partCount) {
        PreparedData preparedData = getPreparedData();

        SellerOffer sellerOfferSample = SellerOffer.builder()
                .comment(faker.lorem().sentence(5))
                .offerStatus(preparedData.offerStatus())
                .creationDateTime(preparedData.creationDT())
                .finishDateTime(preparedData.finishDT())
                .address(preparedData.addressSample())
                .product(preparedData.productSample())
                .seller(preparedData.author())
                .createdAt(Instant.now())
                .build();

        for (int i = 0; i < partCount; i++) {
            sellerOfferSample.addPartOfferToBuy(new PartOfferToBuy());
        }

        sellerOfferSample = testRepositoryHelper.saveSellerOffer(sellerOfferSample);
        assertNotNull(sellerOfferSample);
        assertNotNull(sellerOfferSample.getId());
        return testRepositoryHelper.findByIdWithPartOfferToBuy(sellerOfferSample.getId());

    }

    private static Basket createBasketWithPartOfferToBuy(User buyer, int putToBasketCount, int partCount) {
        SellerOffer sellerOffer = createSellerOffer(partCount);
        List<PartOfferToBuy> availableParts = sellerOffer.getPartOfferToBuyList().stream()
                .filter(part -> part.getStatus().equals(PartOfferToBuyStatus.NOT_RESERVED))
                .limit(putToBasketCount).toList();

        Basket basket = buyer.getBasket();
        for (PartOfferToBuy part : availableParts) {
            basket.addPartOfferToBuy(part);
            part.addBasket(basket);
        }
        basket = testRepositoryHelper.saveBasket(basket);
        assertNotNull(basket.getId());
        assertFalse(basket.getPartOfferToBuySet().isEmpty());
        return basket;
    }

    private static BuyerOrder createBuyerOrderWithParts() {
        User buyer = TestDataUser.getUserPrototype(false);

        Basket basket = new Basket();
        basket.setBuyer(buyer);
        buyer.setBasket(basket);

        buyer = testRepositoryHelper.saveUser(buyer); // Сохранит basket каскадно
        assertNotNull(buyer);
        assertNotNull(buyer.getId());
        basket = buyer.getBasket();
        assertNotNull(basket.getId());

        int partCount = 5;
        int putToBasket = 4;
        int putToOrder = 3;

        basket = createBasketWithPartOfferToBuy(buyer, putToBasket, partCount);
        Set<PartOfferToBuy> partofferToBuySet = basket.getPartOfferToBuySet().stream().limit(putToOrder).collect(Collectors.toSet());

        Payment payment = TestDataPayment.getPaymentCreated();
        BuyerOrder buyerOrder = BuyerOrder.builder()
                .createdAt(Instant.now())
                .build();
        for (PartOfferToBuy partOfferToBuy : partofferToBuySet) {
            buyerOrder.addPartOfferToBuy(partOfferToBuy);
        }
        buyer.addBuyerOrder(buyerOrder);
        payment.addBuyerOrder(buyerOrder);

        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        for (PartOfferToBuy partOfferToBuy : partofferToBuySet) {
            basket.removePartOfferToBuy(partOfferToBuy);
        }
        basket = testRepositoryHelper.saveBasket(basket);

        return buyerOrder;
    }





}
