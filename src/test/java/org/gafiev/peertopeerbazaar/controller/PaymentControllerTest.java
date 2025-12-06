package org.gafiev.peertopeerbazaar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataPayment;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentRedirectResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.dto.error.ErrorResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalPaymentResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuyStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.properties.PaymentProperties;
import org.gafiev.peertopeerbazaar.service.model.interfaces.PaymentService;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
public class PaymentControllerTest extends BaseIntegrationTest {
    public static final String PAYMENT_URI = "www.example.com";
    public static final String PAYMENT_ERROR_USER_MESSAGE = "Can't complete payment.";
    @Autowired
    private PaymentProperties paymentProperties;
    @Autowired
    private PaymentService paymentService;

    @ParameterizedTest()
    @ValueSource(booleans = {true, false})
    @Transactional
    public void getPayment_ByAdminOrBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        // GIVEN
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        Payment payment = buyerOrder.getPayment();
        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertNotNull(payment.getBuyerOrderSet());
        log.info("BuyerOrder Set is : {}", payment.getBuyerOrderSet());

        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        long buyerId = isAdmin ? EXISTING_ADMIN1_ID : buyerOrder.getBuyer().getId();
        String token = JwtTestUtils.generateBearerToken(buyerId, roles, privateKey, keyId);
        //  TEST
        MvcResult result = mockMvc.perform(get("/payment/{id}/user/{buyerId}", payment.getId(), buyerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        PaymentResponse paymentResponse = toDto(responseJson, PaymentResponse.class);
        assertEquals(payment.getBuyerOrderSet().size(), paymentResponse.buyerOrderResponseSet().size());

    }

    @ParameterizedTest()
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    @Transactional
    public void getPaymentWithBuyerOrders_ByAdminOrBuyer_ShouldReturn200(boolean isAdmin, boolean isOrder) throws Exception {
        // GIVEN
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        Payment payment = buyerOrder.getPayment();
        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertNotNull(payment.getBuyerOrderSet());

        log.info("BuyerOrder Set is : {}", payment.getBuyerOrderSet());

        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        long buyerId = isAdmin ? EXISTING_ADMIN1_ID : buyerOrder.getBuyer().getId();
        String token = JwtTestUtils.generateBearerToken(buyerId, roles, privateKey, keyId);
        //  TEST
        MvcResult result = mockMvc.perform(get("/payment/{id}/order/user/{buyerId}", payment.getId(), buyerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .param("isOrder", String.valueOf(isOrder)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        PaymentResponse paymentResponse = toDto(responseJson, PaymentResponse.class);

        if (isOrder) {
            assertNotNull(paymentResponse.buyerOrderResponseSet());
            assertEquals(payment.getBuyerOrderSet().size(), paymentResponse.buyerOrderResponseSet().size());
        }

    }

    @Transactional
    @Test
    public void getAllPayment_ByAdmin_ShouldReturn200() throws Exception {
        // GIVEN
        PaymentFilterRequest filter = TestDataPayment.getPaymentFilterRequest();
        String filterJson = toJson(filter);
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        //  TEST
        MvcResult result = mockMvc.perform(post("/payment/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        Set<PaymentResponse> paymentResponseSet = toDto(responseJson, new TypeReference<>() {
        });
        assertNotNull(paymentResponseSet);
    }

    private static @NotNull Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(false, PaymentStatus.PROCESSING),
                Arguments.of(true, PaymentStatus.SUCCESS),
                Arguments.of(false, PaymentStatus.DENIED)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    @Transactional
    public void completePayment_ByBuyer_ShouldReturn200(boolean isError, PaymentStatus paymentStatus) throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        long paymentId = buyerOrder.getPayment().getId();
        long buyerId = buyerOrder.getBuyer().getId();
        String token = JwtTestUtils.generateBearerToken(buyerId, List.of("USER", "BUYER"), privateKey, keyId);
        //Делаем заглушку к платежной системе
        stubPaymentSystem(buyerOrder.getPayment(), isError, paymentStatus);
        ResultMatcher expectedStatus = isError || paymentStatus == PaymentStatus.DENIED ? status().is5xxServerError() : status().isOk();

        MvcResult result = mockMvc.perform(get("/payment/{id}/complete/user/{buyerId}", paymentId, buyerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        if (isError || paymentStatus == PaymentStatus.DENIED) {
            ErrorResponse errorResponse = toDto(responseJson, ErrorResponse.class);
            assertEquals(PAYMENT_ERROR_USER_MESSAGE, errorResponse.userFriendlyMessage());
            assertNotNull(errorResponse.sourceMessage());
            assertTrue(errorResponse.sourceMessage().contains(isError
                    ? "Can not get payment page Uri from External Payment Service"
                    : "Unsuccessful status"));
        } else {
            PaymentRedirectResponse redirectResponse = toDto(responseJson, PaymentRedirectResponse.class);
            assertEquals(redirectResponse.id(), paymentId);
            assertEquals(PAYMENT_URI, redirectResponse.paymentPageUri());
        }
    }

    @SneakyThrows
    private void stubPaymentSystem(Payment payment, boolean isError, PaymentStatus paymentStatus) {
        String error = isError ? "Text doesn't matter" : null;
        LocalDateTime completionTime = paymentStatus == PaymentStatus.SUCCESS ? LocalDateTime.now() : null;
        String path = new URI(paymentProperties.getClientUri()).getPath() + "/create";
        ExternalPaymentResponse externalPaymentResponse = new ExternalPaymentResponse(PAYMENT_URI,
                paymentStatus, completionTime, error);
        PAYMENT_SERVICE.stubFor(WireMock.post(urlPathEqualTo(path))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalToIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.matchingJsonPath("$.paymentId", equalTo(String.valueOf(payment.getId()))))
                .withRequestBody(WireMock.matchingJsonPath("$.amount", equalTo(payment.getAmount().toPlainString())))
                .withRequestBody(WireMock.matchingJsonPath("$.currency", equalTo(payment.getCurrency().name())))
                .withRequestBody(WireMock.matchingJsonPath("$.callbackUri", WireMock.containing(paymentProperties.getCallbackUri().getPath())))
                .withRequestBody(WireMock.matchingJsonPath("$.returnUri", WireMock.containing(paymentProperties.getReturnUri().getPath())))
                .withRequestBody(WireMock.matchingJsonPath("$.merchantId", equalTo(paymentProperties.getMerchantId())))
                .withRequestBody(WireMock.matchingJsonPath("$.signature"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(toJson(externalPaymentResponse)))
        );
    }

    private static @NotNull Stream<Arguments> getCallbackTestData() {
        return Stream.of(
                Arguments.of("success", 200, "Callback accepted", false),
                Arguments.of("notFound", 404, "Payment is not found", false),
                Arguments.of("error", 500, "Internal Server Error", true)
        );
    }

    @ParameterizedTest
    @MethodSource("getCallbackTestData")
    @Transactional
    public void getCallback_FromPaymentSystem_ShouldReturn200(String caseName, int expectedCode, String expectedAnswer) throws Exception {
        Long paymentId = null;
        BuyerOrder buyerOrder = null;

        switch (caseName) {
            case "success" -> {
                buyerOrder = createBuyerOrderWithParts();
                paymentId = buyerOrder.getPayment().getId();
                System.out.println("PaymentId for success: " + paymentId);
            }
            case "notFound" -> paymentId = 99L;
            case "error" ->   {
                // Для симуляции ошибки (например, EntityNotFoundException при id=0) устанавливаем paymentId=0L
                // Это вызовет исключение в PaymentServiceImpl.updatePayment, так как сущность с id=0 не найдена
                paymentId = 0L;
            }
            default -> throw new IllegalArgumentException("Unknown case: " + caseName);
        }

        ExternalPaymentResponse externalPaymentResponse = new ExternalPaymentResponse(PAYMENT_URI, PaymentStatus.SUCCESS, LocalDateTime.now(), null);
        String responseJson = toJson(externalPaymentResponse);

        MvcResult result = mockMvc.perform(post("/callback/notify/{paymentId}", paymentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(responseJson))
                .andExpect(status().is(expectedCode))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String actualAnswer = result.getResponse().getContentAsString();
        assertNotNull(actualAnswer);
        assertEquals(expectedAnswer, actualAnswer);
    }

    @Test
    @Transactional
    public void deletePayment_ByBuyer_ShouldReturn200() throws Exception {
        // GIVEN
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        Payment payment = buyerOrder.getPayment();
        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertNotNull(payment.getBuyerOrderSet());

        long buyerId = buyerOrder.getBuyer().getId();
        String token = JwtTestUtils.generateBearerToken(buyerId, List.of("BUYER", "USER"), privateKey, keyId);
        //  TEST
        mockMvc.perform(delete("/payment/{id}/user/{buyerId}", payment.getId(), buyerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

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
