package org.gafiev.peertopeerbazaar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataBuyerOrder;
import org.gafiev.peertopeerbazaar.common.TestDataPayment;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.common.TestDeliveryData;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuyStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.gafiev.peertopeerbazaar.common.TestDataUser.BUYER2_ID;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class BuyerOrderControllerTest extends BaseIntegrationTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    void getBuyerOrder_ByBuyerOrAdmin_ShouldReturn200(boolean isAdmin) throws Exception {
        // GIVEN
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        assertNotNull(buyerOrder, "BuyerOrder should exist for the test");
        assertNotNull(buyerOrder.getId(), "BuyerOrder should have Id for the test");

        Long buyerId = buyerOrder.getBuyer().getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        buyerId = isAdmin ? EXISTING_ADMIN1_ID : buyerId;

        String token = JwtTestUtils.generateBearerToken(buyerId, roles, privateKey, keyId);

        // TEST
        MvcResult result = mockMvc.perform(get("/buyerOrder/{id}/user/{buyerId}", buyerOrder.getId(), buyerOrder.getBuyer().getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        BuyerOrderResponse response = toDto(responseJson, BuyerOrderResponse.class);
        assertNotNull(response);
        assertEquals(buyerOrder.getId(), response.id());

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getByUser_AllByStatus_ShouldReturn200(boolean isAdmin) throws Exception {
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        long buyerId = isAdmin ? EXISTING_ADMIN1_ID : BUYER2_ID;

        String token = JwtTestUtils.generateBearerToken(buyerId, roles, privateKey, keyId);
        mockMvc.perform(post("/buyerOrder/status")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("buyerId", String.valueOf(BUYER2_ID))
                        .param("buyerOrderStatus", BuyerOrderStatus.CREATED.name()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllBuyerOrders_ByAdmin_ShouldReturn200() throws Exception {
        BuyerOrderFilterRequest filter = TestDataBuyerOrder.getFilter();
        String filterJson = toJson(filter);
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);

        mockMvc.perform(post("/buyerOrder/filter")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(filterJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    void deleteBuyerOrder_ByAdmin_ShouldReturn200() throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        assertNotNull(buyerOrder.getId());
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        long buyerOrderId = buyerOrder.getId();
        mockMvc.perform(delete("/buyerOrder/{id}", buyerOrderId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        assertTrue(testRepositoryHelper.findById(buyerOrderId).isEmpty());
    }

    @Test
    @Transactional
    void cancelBuyerOrder_ByBuyer_ShouldReturn200() throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();

        String token = JwtTestUtils.generateBearerToken(buyerOrder.getBuyer().getId(), List.of("USER", "BUYER"), privateKey, keyId);

        Optional<BuyerOrder> buyerOrderOptionalBefore = testRepositoryHelper.findByIdWithBuyer(buyerOrder.getId());
        assertTrue(buyerOrderOptionalBefore.isPresent(), "BuyerOrder should exist before cancel");
        BuyerOrder buyerOrderBefore = buyerOrderOptionalBefore.get();
        BuyerOrderStatus orderStatusBefore = buyerOrderBefore.getBuyerOrderStatus();
        int originalRating = buyerOrderBefore.getBuyer().getRatingBuyer();

        mockMvc.perform(patch("/buyerOrder/cancel/{id}", buyerOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("buyerId", String.valueOf(buyerOrder.getBuyer().getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().bytes(new byte[0]));

        Optional<BuyerOrder> buyerOrderOptionalAfter = testRepositoryHelper.findByIdWithBuyer(buyerOrder.getId());
        assertTrue(buyerOrderOptionalAfter.isPresent(), "BuyerOrder should still exist after cancel");
        BuyerOrder buyerOrderAfter = buyerOrderOptionalAfter.get();
        assertNotEquals(buyerOrderAfter.getBuyerOrderStatus(), orderStatusBefore);
//        assertNotEquals(buyerOrderAfter.getBuyerOrderStatus(), buyerOrderBefore.getBuyerOrderStatus()); // Почему пропал?
        assertEquals(buyerOrderAfter.getBuyerOrderStatus(), BuyerOrderStatus.DENIED);
        log.info("Actual getBuyerOrderStatus : {} ", buyerOrderAfter.getBuyerOrderStatus());

        assertEquals(originalRating - 1, buyerOrderAfter.getBuyer().getRatingBuyer());
        log.info("Actual buyer rating is : {}", buyerOrderAfter.getBuyer().getRatingBuyer());
    }

    @Test
    @Transactional
    void createBuyerOrder_ByBuyer_ShouldReturn200() throws Exception {
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
        Set<Long> partOfferToBuyIds = partofferToBuySet.stream().map(PartOfferToBuy::getId).collect(Collectors.toSet());

        BuyerOrderCreateRequest orderCreateRequest = new BuyerOrderCreateRequest(partOfferToBuyIds);
        String orderCreateJson = toJson(orderCreateRequest);

        String token = JwtTestUtils.generateBearerToken(buyer.getId(), List.of("USER", "BUYER"), privateKey, keyId);

        MvcResult result = mockMvc.perform(post("/buyerOrder/create")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderCreateJson)
                        .param("buyerId", String.valueOf(buyer.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        Set<BuyerOrderResponse> responseSet = toDto(responseJson, new TypeReference<>() {
        });
        assertNotNull(responseSet);
        assertEquals(1, responseSet.size());
        assertOrderResponse(responseSet.stream().findFirst().orElseThrow(), buyer.getId(), putToOrder);

    }

    private void assertOrderResponse(BuyerOrderResponse response, long buyerId, int putToOrder) {
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(buyerId, response.buyerId());
        assertEquals(BuyerOrderStatus.CREATED, response.status());
        assertNotNull(response.paymentId());
        assertNotNull(response.partOfferToBuyResponseSet());
        assertEquals(putToOrder, response.partOfferToBuyResponseSet().size());
        assertTrue(response.deliveryIds() == null || response.deliveryIds().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void updateBuyerOrder_ByAdminOrBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        User buyer = buyerOrder.getBuyer();
        assertNotNull(buyer.getId());
        long buyerId = buyer.getId();
        long userId = isAdmin ? EXISTING_ADMIN1_ID : buyerId;
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        Delivery delivery1 = TestDeliveryData.getDeliverySample();
        buyerOrder.addDelivery(delivery1);

        Delivery delivery2 = TestDeliveryData.getDeliverySample();
        delivery2.setDeliveryStatus(DeliveryStatus.CREATED);
        buyerOrder.addDelivery(delivery2);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        Set<Delivery> deliverySet = buyerOrder.getDeliverySet();
        assertEquals(2, deliverySet.size());



        BuyerOrder sourceBuyerOrder = createBuyerOrderWithParts();
        sourceBuyerOrder.setBuyer(buyer);
        Delivery delivery3 = TestDeliveryData.getDeliverySample();
        sourceBuyerOrder.addDelivery(delivery3);
        sourceBuyerOrder = testRepositoryHelper.saveBuyerOrder(sourceBuyerOrder);

        long deliveryIdToRemove = deliverySet.stream().findFirst().map(Delivery::getId).orElseThrow();
        long deliveryIdToAdd = sourceBuyerOrder.getDeliverySet().stream().findFirst().map(Delivery::getId).orElseThrow();

        BuyerOrderUpdateRequest updateRequest = new BuyerOrderUpdateRequest(Set.of(deliveryIdToRemove), Set.of(deliveryIdToAdd), null, null);
        String updateRequestJson = toJson(updateRequest);
        MvcResult result = mockMvc.perform(put("/buyerOrder/{id}/user/{buyerId}", buyerOrder.getId(),buyerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String resultJson = result.getResponse().getContentAsString();
        BuyerOrderResponse buyerOrderResponse = toDto(resultJson,BuyerOrderResponse.class);
        assertNotNull(buyerOrderResponse);
        assertEquals(buyerOrder.getId(),buyerOrderResponse.id());
        assertTrue(buyerOrderResponse.deliveryIds().contains(deliveryIdToAdd));
        assertFalse(buyerOrderResponse.deliveryIds().contains(deliveryIdToRemove));
    }


    private Basket createBasketWithPartOfferToBuy(User buyer, int putToBasketCount, int partCount) {
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

    private SellerOffer createSellerOffer(int partCount) {
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

    private BuyerOrder createBuyerOrderWithParts() {
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
