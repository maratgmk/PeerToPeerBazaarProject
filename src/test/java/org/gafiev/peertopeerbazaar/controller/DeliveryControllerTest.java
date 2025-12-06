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
import org.gafiev.peertopeerbazaar.common.TestDeliveryData;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryUpdateTime;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
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
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.properties.DroneProperties;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class DeliveryControllerTest extends BaseIntegrationTest {

    @Autowired
    private DroneProperties droneProperties;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void getDelivery_ByAdminOrBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        //  GIVEN
        Delivery delivery = createDelivery();
        assertNotNull(delivery);
        assertNotNull(delivery.getId());

        Address toAddress = delivery.getToAddress();

        User user = delivery.getBuyerOrder().getBuyer();
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());

        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER", "SELLER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        //  TEST
        MvcResult mvcResult = mockMvc.perform(get("/delivery/{id}/user/{userId}", delivery.getId(), userId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();
        DeliveryResponse deliveryResponse = toDto(responseJson, DeliveryResponse.class);
        assertEquals(delivery.getBuyerOrder().getId(), deliveryResponse.orderId());
        assertEquals(toAddress.getId(), deliveryResponse.addressId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void getDeliveriesByOrderId_ByAdminOrBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        Address fromAddress = buyerOrder.getPartOfferToBuySet().stream().findFirst().orElseThrow().getSellerOffer().getAddress();
        Address toAddress = TestDataAddress.getAddressSample();
        toAddress = testRepositoryHelper.saveAddress(toAddress);
        assertNotNull(toAddress.getId());

        Delivery delivery1 = TestDeliveryData.getDeliverySample();
        delivery1.setBuyerOrder(buyerOrder);
        delivery1.setFromAddress(fromAddress);
        delivery1.setToAddress(toAddress);
        buyerOrder.addDelivery(delivery1);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        delivery1 = testRepositoryHelper.saveDelivery(delivery1);
        assertNotNull(delivery1);
        assertNotNull(delivery1.getId());

        Delivery delivery2 = TestDeliveryData.getDeliverySample();
        delivery2.setBuyerOrder(buyerOrder);
        delivery2.setFromAddress(fromAddress);
        delivery2.setToAddress(toAddress);
        buyerOrder.addDelivery(delivery2);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        delivery2 = testRepositoryHelper.saveDelivery(delivery2);
        assertNotNull(delivery2);
        assertNotNull(delivery2.getId());

        int numberOfDelivery = buyerOrder.getDeliverySet().size();

        User user = buyerOrder.getBuyer();
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());

        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER", "SELLER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        MvcResult mvcResult = mockMvc.perform(get("/delivery/order/{buyerOrderId}/user/{userId}", buyerOrder.getId(), userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Set<DeliveryResponse> deliverySet = toDto(responseJson, new TypeReference<Set<DeliveryResponse>>() {
        });

        assertEquals(numberOfDelivery, deliverySet.size());
        long expectedToAddressId = toAddress.getId();
        System.out.println("Expected addressId: " + expectedToAddressId);
        deliverySet.forEach(dr -> System.out.println("Actual addressId in response: " + dr.addressId()));
        assertTrue(deliverySet.stream().allMatch(deliveryResponse -> deliveryResponse.addressId().equals(expectedToAddressId)),
                "All deliveries have only the same  addressId" + expectedToAddressId);
    }

    @Test
    @Transactional
    public void getAllDeliveriesByFilter_ByAdmin_ShouldReturn200() throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        Address fromAddress = buyerOrder.getPartOfferToBuySet().stream().findFirst().orElseThrow().getSellerOffer().getAddress();
        Address toAddress = TestDataAddress.getAddressSample();
        toAddress = testRepositoryHelper.saveAddress(toAddress);
        assertNotNull(toAddress.getId());

        Delivery delivery1 = TestDeliveryData.getDeliverySample();
        delivery1.setBuyerOrder(buyerOrder);
        delivery1.setFromAddress(fromAddress);
        delivery1.setToAddress(toAddress);
        delivery1.setDeliveryStatus(DeliveryStatus.DELIVERED);
        delivery1.setTimeSlot(new TimeSlot(LocalDateTime.of(2025, 11, 21, 10, 30, 00),
                LocalDateTime.of(2025, 11, 21, 11, 30, 00)));
        buyerOrder.addDelivery(delivery1);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        delivery1 = testRepositoryHelper.saveDelivery(delivery1);
        assertNotNull(delivery1);
        assertNotNull(delivery1.getId());

        Delivery delivery2 = TestDeliveryData.getDeliverySample();
        delivery2.setBuyerOrder(buyerOrder);
        delivery2.setFromAddress(fromAddress);
        delivery2.setToAddress(toAddress);
        delivery2.setDeliveryStatus(DeliveryStatus.DELAYED);
        delivery2.setTimeSlot(new TimeSlot(LocalDateTime.of(2025, 11, 21, 7, 30, 00),
                LocalDateTime.of(2025, 11, 21, 11, 30, 00)));
        buyerOrder.addDelivery(delivery2);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        delivery2 = testRepositoryHelper.saveDelivery(delivery2);
        assertNotNull(delivery2);
        assertNotNull(delivery2.getId());

        Delivery delivery3 = TestDeliveryData.getDeliverySample();
        delivery3.setBuyerOrder(buyerOrder);
        delivery3.setFromAddress(fromAddress);
        delivery3.setToAddress(toAddress);
        delivery3.setDeliveryStatus(DeliveryStatus.DELAYED);
        delivery3.setTimeSlot(new TimeSlot(LocalDateTime.of(2025, 11, 21, 10, 30, 00),
                LocalDateTime.of(2025, 11, 21, 11, 30, 00)));
        buyerOrder.addDelivery(delivery3);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        delivery3 = testRepositoryHelper.saveDelivery(delivery3);
        assertNotNull(delivery3);
        assertNotNull(delivery3.getId());

        Drone drone = TestDataDrone.getDroneSample();
        drone.addDelivery(delivery1);
        drone.addDelivery(delivery2);
        drone.addDelivery(delivery3);
        drone = testRepositoryHelper.saveDrone(drone);
        assertNotNull(drone.getId());


        System.out.println("Created deliveries:");
        System.out.println("Delivery1: ID=" + delivery1.getId() + ", Status=" + delivery1.getDeliveryStatus() + ", BuyerOrderId=" + delivery1.getBuyerOrder().getId() + ", ToAddressId=" + delivery1.getToAddress().getId() + ", FromAddressId=" + delivery1.getFromAddress().getId() + ", TimeSlot=" + delivery1.getTimeSlot() + ", DroneId=" + (delivery1.getDrone() != null ? delivery1.getDrone().getId() : "null"));
        System.out.println("Delivery2: ID=" + delivery2.getId() + ", Status=" + delivery2.getDeliveryStatus() + ", BuyerOrderId=" + delivery2.getBuyerOrder().getId() + ", ToAddressId=" + delivery2.getToAddress().getId() + ", FromAddressId=" + delivery2.getFromAddress().getId() + ", TimeSlot=" + delivery2.getTimeSlot() + " DroneId=" + (delivery2.getDrone() != null ? delivery2.getDrone().getId() : "null"));
        System.out.println("Delivery3: ID=" + delivery3.getId() + ", Status=" + delivery3.getDeliveryStatus() + ", BuyerOrderId=" + delivery3.getBuyerOrder().getId() + ", ToAddressId=" + delivery3.getToAddress().getId() + ", FromAddressId=" + delivery3.getFromAddress().getId() + ", TimeSlot=" + delivery1.getTimeSlot() + " DroneId=" + (delivery3.getDrone() != null ? delivery3.getDrone().getId() : "null"));
        System.out.println("Drone ID: " + drone.getId());

        DeliveryFilterRequest filterRequest = DeliveryFilterRequest.builder()
                .ids(Set.of(delivery1.getId(), delivery2.getId(), delivery3.getId()))
                .buyerOrderId(buyerOrder.getId())
                .deliveryStatus(DeliveryStatus.DELAYED)
                .fromAddressId(fromAddress.getId())
                .toAddressId(toAddress.getId())
                .startTimeAfter(new TimeSlot(LocalDateTime.of(2025, 11, 21, 8, 15, 00),
                        LocalDateTime.of(2025, 11, 21, 9, 45, 00)))
                .endTimeBefore(new TimeSlot(LocalDateTime.of(2025, 11, 21, 13, 15, 00),
                        LocalDateTime.of(2025, 11, 21, 14, 45, 00)))
                .droneId(drone.getId())
                .build();

        String filterJson = toJson(filterRequest);

        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        MvcResult result = mockMvc.perform(post("/delivery/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        Set<DeliveryResponse> deliveryResponses = toDto(resultJson, new TypeReference<Set<DeliveryResponse>>() {
        });
        DeliveryResponse deliveryResponse3 = deliveryMapper.toDeliveryResponse(delivery3);
        assertEquals(Set.of(deliveryResponse3), deliveryResponses);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void createDelivery_ByBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        buyerOrder.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);

        User buyer = buyerOrder.getBuyer();
        assertNotNull(buyer.getId());
        Address address = TestDataAddress.getAddressSample();
        address = testRepositoryHelper.saveAddress(address);
        assertNotNull(address.getId());

        DeliveryCreateRequest createRequest = new DeliveryCreateRequest(buyerOrder.getId(), address.getId());
        String createJson = toJson(createRequest);

        long userId = isAdmin ? EXISTING_ADMIN1_ID : buyer.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");

        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);
        mockMvc.perform(post("/delivery/user/{buyerId}", buyer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.deliveryStatus").value(DeliveryStatus.CREATED.name()))
                .andExpect(jsonPath("$.orderId").value(buyerOrder.getId()))
                .andExpect(jsonPath("$.addressId").value(address.getId()))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void getTimeSlots_ByBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        Delivery delivery = createDelivery();
        User buyer = delivery.getBuyerOrder().getBuyer();
        assertNotNull(buyer.getId());

        stubDroneOperator(delivery);

        long userId = isAdmin ? EXISTING_ADMIN1_ID : buyer.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        MvcResult result = mockMvc.perform(get("/delivery/{id}/time/user/{buyerId}", delivery.getId(), buyer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        List<TimeSlotResponse> slots = toDto(resultJson, new TypeReference<List<TimeSlotResponse>>() {
        });
        assertFalse(slots.isEmpty());
        assertEquals(2, slots.size());
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
                        .withBody(toJson(TestDataDrone.getListTimeSlots()))));
        log.info("WireMock stub created for path: {} with expected body: {}", path, deliveryDroneRequestJson);
        log.info("Response body: {}", toJson(TestDataDrone.getListTimeSlots()));
    }

    @Test
    @Transactional
    public void assignDrone_ByBuyer_ShouldReturn200() throws Exception {
        Delivery delivery = createDelivery();
        User buyer = delivery.getBuyerOrder().getBuyer();
        assertNotNull(buyer.getId());
        DeliveryUpdateTime updateTime = new DeliveryUpdateTime(new TimeSlotResponse(
                LocalDateTime.of(2025, 12, 04, 14, 30, 00),
                LocalDateTime.of(2025, 12, 04, 16, 39, 00)));
        String updateTimeJson = toJson(updateTime);

        stubAssignDroneOperator(delivery, updateTime);

        String token = JwtTestUtils.generateBearerToken(buyer.getId(), List.of("USER", "BUYER"), privateKey, keyId);

        MvcResult result = mockMvc.perform(put("/delivery/{id}/user/{buyerId}", delivery.getId(), buyer.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTimeJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseResult = result.getResponse().getContentAsString();
        DeliveryResponse deliveryResponse = toDto(responseResult, DeliveryResponse.class);

        assertEquals(DeliveryStatus.DRONE_ASSIGNED, deliveryResponse.deliveryStatus());
        assertEquals(timeSlotMapper.toTimeSlot(updateTime.timeSlot()), deliveryResponse.timeSlot());
        assertEquals(delivery.getBuyerOrder().getId(), deliveryResponse.orderId());
        assertEquals(delivery.getToAddress().getId(), deliveryResponse.addressId());

    }

    @SneakyThrows
    private void stubAssignDroneOperator(Delivery delivery, DeliveryUpdateTime updateTime) {
        TimeSlotResponse timeSlotResponse = updateTime.timeSlot();
        delivery.setTimeSlot(timeSlotMapper.toTimeSlot(timeSlotResponse));

        DeliveryDroneRequest deliveryDroneRequest = deliveryMapper.toDeliveryDroneRequest(delivery);
        String deliveryDroneRequestJson = toJson(deliveryDroneRequest);
        log.info("Mapped deliveryDroneRequest to Json : {}  ", deliveryDroneRequestJson);

        DroneResponse droneResponse = DroneResponse.builder()
                .status(DroneStatus.ASSIGNED)
                .droneServiceId(1891L)
                .deliveryIds(Set.of(delivery.getId()))
                .build();

        String path = new URI(droneProperties.getClientUri()).getPath() + "/assign";

        DRONE_OPERATOR.stubFor(WireMock.post(urlPathEqualTo(path))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalToIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(deliveryDroneRequestJson))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(toJson(droneResponse))));

        log.info("WireMock stub created for path: {} with expected body: {}", path, deliveryDroneRequestJson);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @Transactional
    public void cancelMyDelivery_ByAdminOrBuyer_ShouldReturn200(boolean isAdmin) throws Exception {
        BuyerOrder buyerOrder = createBuyerOrderWithParts();
        buyerOrder.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
        buyerOrder = testRepositoryHelper.saveBuyerOrder(buyerOrder);
        assertNotNull(buyerOrder.getId());
        Address fromAddress = buyerOrder.getPartOfferToBuySet().stream().findFirst().orElseThrow().getSellerOffer().getAddress();
        Address toAddress = TestDataAddress.getAddressSample();
        toAddress = testRepositoryHelper.saveAddress(toAddress);
        assertNotNull(toAddress.getId());
        Drone drone = TestDataDrone.getDroneSample();
        Delivery delivery = Delivery.builder()
                .timeSlot(null)
                .deliveryStatus(DeliveryStatus.DRONE_ASSIGNED)
                .buyerOrder(buyerOrder)
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .drone(drone)
                .createdAt(Instant.now().minusSeconds(600))
                .build();
        delivery = testRepositoryHelper.saveDelivery(delivery);
        assertNotNull(delivery.getId());
        assertNotNull(drone.getDroneServiceId());
        assertNotNull(drone.getId());

        User buyer = delivery.getBuyerOrder().getBuyer();
        assertNotNull(buyer.getId());

        stubForCancel(delivery);

        long userId = isAdmin ? EXISTING_ADMIN1_ID : buyer.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        MvcResult result = mockMvc.perform(get("/delivery/{id}/cancel/user/{buyerId}", delivery.getId(), userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String responseResult = result.getResponse().getContentAsString();
        DeliveryResponse deliveryResponse = toDto(responseResult, DeliveryResponse.class);
        assertNotNull(deliveryResponse);
        assertEquals(delivery.getId(), deliveryResponse.id());
        assertEquals(DeliveryStatus.CANCELLED_BY_BUYER, deliveryResponse.deliveryStatus());
    }

    @SneakyThrows
    private void stubForCancel(Delivery delivery) {
        long deliveryId = delivery.getId();
        long droneServiceId = delivery.getDrone().getDroneServiceId();

        String path = new URI(droneProperties.getClientUri()).getPath() + "/cancel/" + droneServiceId;
        DroneResponse droneResponse = DroneResponse.builder()
                .status(DroneStatus.BACK_TO_BASE)
                .droneServiceId(droneServiceId)
                .deliveryIds(Set.of(deliveryId))
                .build();

        DRONE_OPERATOR.stubFor(WireMock.get(urlPathEqualTo(path))
                .withQueryParam("deliveryId", equalToIgnoreCase(String.valueOf(deliveryId)))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(toJson(droneResponse))));
    }

    @Test
    @Transactional
    public void deleteDeliveryById_ByAdmin_ShouldReturn200() throws Exception {
        Delivery delivery = createDelivery();
        assertNotNull(delivery);
        assertNotNull(delivery.getId());
        long deliveryId = delivery.getId();

        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        mockMvc.perform(delete("/delivery/{id}", deliveryId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));
        assertTrue(testRepositoryHelper.findDeliveryById(deliveryId).isEmpty());
    }

    private Delivery createDelivery() {
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
