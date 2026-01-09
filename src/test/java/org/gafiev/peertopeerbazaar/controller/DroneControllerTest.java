package org.gafiev.peertopeerbazaar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataAddress;
import org.gafiev.peertopeerbazaar.common.TestDataDrone;
import org.gafiev.peertopeerbazaar.common.TestDataPayment;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.error.ErrorResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuyStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.mapper.DeliveryMapper;
import org.gafiev.peertopeerbazaar.properties.DroneProperties;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class DroneControllerTest extends BaseIntegrationTest {

    @Autowired
    private DroneProperties droneProperties;
    @Autowired
    private DeliveryMapper deliveryMapper;

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ADMIN", "BUYER", "SELLER"})
    public void getDroneByAdminOrUser_ShouldReturn200(Role role) throws Exception {
        //  GIVEN
        Drone drone = TestDataDrone.getDroneSample();
        drone = testRepositoryHelper.saveDrone(drone);
        assertNotNull(drone);
        assertNotNull(drone.getId());

        User user = TestDataUser.getUserPrototype(role == Role.SELLER);
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());

        long userId = role == Role.ADMIN ? EXISTING_ADMIN1_ID : user.getId();
        List<String> roles = List.of("USER", role.name());
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        //  TEST
        MvcResult droneResult = mockMvc.perform(get("/drone/{id}/user/{userId}", drone.getId(), userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String droneJson = droneResult.getResponse().getContentAsString();
        DroneResponse droneResponse = toDto(droneJson, DroneResponse.class);
        assertEquals(drone.getId(), droneResponse.id());
        assertEquals(drone.getDroneStatus(), droneResponse.status());
    }

    private static @NotNull Stream<DroneFilterRequest> getDroneFilterTestData() {
        return Stream.of(TestDataDrone.FILTER_BY_IDS, TestDataDrone.FILTER_BY_SERVICE_IDS, TestDataDrone.FILTER_BY_DELIVERY_IDS);
    }

    @ParameterizedTest
    @MethodSource("getDroneFilterTestData")
    public void getAllDrones_ByAdmin_ShouldReturn200(DroneFilterRequest filterRequest) throws Exception {
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        String filterJson = toJson(filterRequest);

        MvcResult result = mockMvc.perform(post("/drone/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        Set<DroneResponse> droneResponseSet = toDto(resultJson, new TypeReference<Set<DroneResponse>>() {
        });
        assertNotNull(droneResponseSet);
        assertFalse(droneResponseSet.isEmpty());
        if (filterRequest.droneIds() != null && !filterRequest.droneIds().isEmpty()) {
            assertEquals(filterRequest.droneIds().size(), droneResponseSet.size());
            assertTrue(filterRequest.droneIds().containsAll(droneResponseSet.stream().map(DroneResponse::id).toList()));
        } else if (filterRequest.droneServiceIds() != null && !filterRequest.droneServiceIds().isEmpty()) {
            assertEquals(filterRequest.droneServiceIds().size(), droneResponseSet.size());
            assertTrue(filterRequest.droneServiceIds().containsAll(droneResponseSet.stream().map(DroneResponse::droneServiceId).toList()));
        }

    }

    private static @NotNull Stream<Arguments> getDroneTestData() {
        return Stream.of(
                Arguments.of(false, true),
                Arguments.of(false, false),
                Arguments.of(true, false),
                Arguments.of(true, true)
        );
    }

    @ParameterizedTest
    @MethodSource("getDroneTestData")
    @Transactional
    public void cancelDrone(boolean isError, boolean isAdmin) throws Exception {
        System.out.println("Start Test for Cancel Drone !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
        //  GIVEN
        Drone drone = TestDataDrone.getDroneSample();
        Delivery delivery = createDelivery();
        drone.addDelivery(delivery);

        drone = testRepositoryHelper.saveDrone(drone);
        assertNotNull(drone);
        assertNotNull(drone.getId());

        User user = delivery.getBuyerOrder().getBuyer();
        assertNotNull(user);
        assertNotNull(user.getId());

        Long deliveryId = drone.getDeliverySet().stream().map(Delivery::getId).findFirst().orElseThrow();
        stubDroneOperator(drone, isError, deliveryId);
        System.out.println("Input stubDroneOperator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        ResultMatcher expectedStatus = isError ? status().is5xxServerError() : status().isOk();

        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER", "SELLER");
        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);
        //  TEST
        MvcResult mvcResult = mockMvc.perform(get("/drone/cancel/{id}/user/{userId}", drone.getId(), userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .param("deliveryId", String.valueOf(deliveryId)))
                .andExpect(expectedStatus)
                .andDo(print())
                .andReturn();

        // ВЕРИФИКАЦИЯ: Проверяем, что запрос дошёл до WireMock
        // Пересчитываем path тем же способом, что в stubDroneOperator
        String path = null;
        try {
            path = new URI(droneProperties.getClientUri()).getPath() + "/cancel/" + drone.getDroneServiceId();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI in droneProperties", e);
        }
        System.out.println("Verifying request to path: " + path + " with deliveryId: " + deliveryId);
        DRONE_OPERATOR.verify(getRequestedFor(urlPathEqualTo(path))
                .withQueryParam("deliveryId", equalTo(String.valueOf(deliveryId))));


        String resultJson = mvcResult.getResponse().getContentAsString();
        if (isError) {
            ErrorResponse errorResponse = toDto(resultJson, ErrorResponse.class);
            assertNotNull(errorResponse);
            assertEquals("Cannot request a drone for delivery.", errorResponse.userFriendlyMessage());

        } else {
            DroneResponse droneResponse = toDto(resultJson, DroneResponse.class);
            assertNotNull(droneResponse);
            assertEquals(DroneStatus.BACK_TO_BASE, droneResponse.status());
        }
    }

    @SneakyThrows
    private void stubDroneOperator(@NotNull Drone drone, boolean isError, Long deliveryId) {
        System.out.println("Start stub for DroneOperator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        String error = isError ? "Drone is not found : droneServiceId = " + drone.getDroneServiceId() : null;

//        String path = new URI(droneProperties.getClientUri()).getPath() + "/cancel/%d".formatted(drone.getDroneServiceId());
        String path = new URI(droneProperties.getClientUri()).getPath() + "/cancel/" + drone.getDroneServiceId();
        ExternalDroneResponse externalDroneResponse = ExternalDroneResponse.builder()
                .droneServiceId(drone.getDroneServiceId())
                .droneStatus(DroneStatus.BACK_TO_BASE)
                .errorMessage(error)
                .build();
        int responseStatus = isError ? 500 : 200;
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("Base Uniform Resource Locator  for drone service : " + DRONE_OPERATOR.baseUrl());

        DRONE_OPERATOR.stubFor(WireMock.get(urlPathEqualTo(path))
                .withQueryParam("deliveryId", equalTo(String.valueOf(deliveryId)))
                .willReturn(WireMock.aResponse()
                        .withStatus(responseStatus)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(isError ? "{}" : toJson(externalDroneResponse))));
        System.out.println("Finish stub for DroneOperator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void getTimeSlots_ByAdminOrBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        System.out.println("Test started");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        Delivery delivery = createDelivery();
        assertNotNull(delivery);
        assertNotNull(delivery.getId());

        User user = delivery.getBuyerOrder().getBuyer();
        assertNotNull(user.getId());
        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        stubDroneOperator(delivery);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        MvcResult mvcResult = mockMvc.perform(get("/drone/delivery/{deliveryId}/user/{userId}", delivery.getId(), userId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<TimeSlotResponse> slots = toDto(response, new TypeReference<List<TimeSlotResponse>>() {
        });

        //      ASSERTIONS
        assertNotNull(slots);
        assertFalse(slots.isEmpty());
        assertEquals(1, slots.size(), "Ожидается 1 слот");  // Из WireMock стуба
        TimeSlotResponse slot = slots.get(0);
        assertNotNull(slot.start());
        assertNotNull(slot.end());
        System.out.println("Десериализованные слоты: " + slots);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @SneakyThrows
    private void stubDroneOperator(Delivery delivery) {
        DeliveryDroneRequest deliveryDroneRequest = deliveryMapper.toDeliveryDroneRequest(delivery);
        String deliveryDroneRequestJson = toJson(deliveryDroneRequest);
        log.info("Mapped deliveryDroneRequest to Json : {}  ", deliveryDroneRequestJson);

        String path = new URI(droneProperties.getClientUri()).getPath() + "/timeSlot";

        DRONE_OPERATOR.stubFor(WireMock.post(urlPathEqualTo(path))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalToIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(deliveryDroneRequestJson))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(toJson(Set.of(TestDataDrone.getTimeSlotSample())))));

        log.info("WireMock stub created for path: {} with expected body: {}", path, deliveryDroneRequestJson);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void getUpdateDrone_ByAdminOrUser_ShouldReturn200(boolean isAdmin) throws Exception {
        //  GIVEN
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2025, 11, 20, 9, 30, 00),
                LocalDateTime.of(2025, 11, 20, 12, 10, 00));
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        Delivery delivery1 = Delivery.builder()
                .buyerOrder(buyerOrder)
                .deliveryStatus(DeliveryStatus.CREATED)
                .toAddress(TestDataAddress.getAddressSample())
                .fromAddress(TestDataAddress.getAddressSample())
                .timeSlot(timeSlot)
                .createdAt(Instant.now().minusSeconds(3600))
                .build();
        delivery1 = testRepositoryHelper.saveDelivery(delivery1);
        assertNotNull(delivery1);
        assertNotNull(delivery1.getId());

        Delivery delivery2 = Delivery.builder()
                .buyerOrder(buyerOrder)
                .deliveryStatus(DeliveryStatus.CREATED)
                .toAddress(TestDataAddress.getAddressSample())
                .fromAddress(TestDataAddress.getAddressSample())
                .timeSlot(timeSlot)
                .createdAt(Instant.now().minusSeconds(2400))
                .build();
        delivery2 = testRepositoryHelper.saveDelivery(delivery2);
        assertNotNull(delivery2);
        assertNotNull(delivery2.getId());

        Delivery delivery3 = Delivery.builder()
                .buyerOrder(buyerOrder)
                .deliveryStatus(DeliveryStatus.CREATED)
                .toAddress(TestDataAddress.getAddressSample())
                .fromAddress(TestDataAddress.getAddressSample())
                .timeSlot(timeSlot)
                .createdAt(Instant.now().minusSeconds(1800))
                .build();
        delivery3 = testRepositoryHelper.saveDelivery(delivery3);
        assertNotNull(delivery3);
        assertNotNull(delivery3.getId());

        Delivery delivery4 = Delivery.builder()
                .buyerOrder(buyerOrder)
                .deliveryStatus(DeliveryStatus.CREATED)
                .toAddress(TestDataAddress.getAddressSample())
                .fromAddress(TestDataAddress.getAddressSample())
                .timeSlot(timeSlot)
                .createdAt(Instant.now().minusSeconds(600))
                .build();
        delivery4 = testRepositoryHelper.saveDelivery(delivery4);
        assertNotNull(delivery4);
        assertNotNull(delivery4.getId());

        Drone drone = TestDataDrone.getDroneSample();
        drone.setDeliverySet(Set.of(delivery1, delivery2));
        drone = testRepositoryHelper.saveDrone(drone);
        assertNotNull(drone);
        assertNotNull(drone.getId());

        log.info("Drone has id : {} and deliveryIds : {} ",drone.getId(),drone.getDeliverySet().stream().map(Delivery::getId).toList());

        User user = TestDataUser.getUserPrototype(false);
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());

        DroneUpdateRequest droneUpdateRequest = TestDataDrone.getDroneCreateRequest(Set.of(
                delivery1.getId(), delivery2.getId()), Set.of(delivery3.getId(), delivery4.getId()));
        String requestJson = toJson(droneUpdateRequest);

        // TEST
        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        MvcResult mvcResult = mockMvc.perform(put("/drone/{id}/user/{userId}", drone.getId(), userId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();
        DroneResponse droneResponse = toDto(responseJson, DroneResponse.class);
        // ASSIGNMENT
        assertEquals(droneResponse.deliveryIds(), Set.of(delivery3.getId(), delivery4.getId()));
        assertNotEquals(droneResponse.deliveryIds(), Set.of(delivery1.getId(), delivery2.getId()));

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

    public Delivery createDelivery() {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        buyerOrder.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);
        assertNotNull(buyerOrder.getId());
        Address fromAddress = buyerOrder.getPartOfferToBuySet().stream().findFirst().orElseThrow().getSellerOffer().getAddress();
        Address toAddress = TestDataAddress.getAddressSample();
        toAddress = testRepositoryHelper.saveAddress(toAddress);
        assertNotNull(toAddress.getId());
        Delivery delivery = Delivery.builder()
                .timeSlot(null)
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
}

