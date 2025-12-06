package org.gafiev.peertopeerbazaar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataMapper;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.SellerOfferFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.SellerOfferResponse;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.repository.SellerOfferRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.gafiev.peertopeerbazaar.common.TestDataSellerOffer.EXIST_SELLER_OFFER_ID1;
import static org.gafiev.peertopeerbazaar.common.TestDataSellerOffer.EXIST_SELLER_OFFER_ID2;
import static org.gafiev.peertopeerbazaar.common.TestDataSellerOffer.EXIST_SELLER_OFFER_ID3;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.USER1_ID;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.matchesRegex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SellerOfferControllerTest extends BaseIntegrationTest {
    @Autowired
    private SellerOfferRepository sellerOfferRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getSellerOfferSample_ByAdmin_ShouldReturn200() throws Exception {
        // GIVEN
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

        sellerOfferSample = sellerOfferRepository.save(sellerOfferSample);
        assertNotNull(sellerOfferSample);
        assertNotNull(sellerOfferSample.getId());

        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").withZone(ZoneOffset.UTC);
        String expectedCreationDateTime = sellerOfferSample.getCreationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        String expectedFinishDateTime = sellerOfferSample.getFinishDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        String expectedCreatedAt = ZonedDateTime.ofInstant(sellerOfferSample.getCreatedAt(), ZoneOffset.UTC).format(formatter);  // Преобразуем Instant в ZonedDateTime и форматируем

        mockMvc.perform(get("/sellerOffer/{id}/user/{userId}", sellerOfferSample.getId(), EXISTING_ADMIN1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.finishDateTime").exists())
                .andExpect(jsonPath("$.finishDateTime").value(expectedFinishDateTime))
                .andExpect(jsonPath("$.creationDateTime").value(expectedCreationDateTime))
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt))
                .andDo(print());
    }

    @Test
    void getSellerOfferSample_ByAnybody_ShouldReturn200() throws Exception {
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

        sellerOfferSample = sellerOfferRepository.save(sellerOfferSample);
        assertNotNull(sellerOfferSample);
        assertNotNull(sellerOfferSample.getId());


        User prototype = TestDataUser.getUserPrototype(false);
        prototype = userRepository.save(prototype);
        assertNotNull(prototype);
        assertNotNull(prototype.getId());
        List<String> listRoles = prototype.getRoles().stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(prototype.getId(), listRoles, privateKey, keyId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").withZone(ZoneOffset.UTC);
        String expectedCreationDateTime = sellerOfferSample.getCreationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        String expectedFinishDateTime = sellerOfferSample.getFinishDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        String expectedCreatedAt = ZonedDateTime.ofInstant(sellerOfferSample.getCreatedAt(), ZoneOffset.UTC).format(formatter);

        mockMvc.perform(get("/sellerOffer/{id}/user/{userId}", sellerOfferSample.getId(), prototype.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.finishDateTime").value(expectedFinishDateTime))
                .andExpect(jsonPath("$.creationDateTime").value(expectedCreationDateTime))
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt))
                .andDo(print());
    }

    @Test
    void getExistSellerOffer_ByAdmin_ShouldReturn200() throws Exception {
        SellerOffer existOffer = sellerOfferRepository.findById(EXIST_SELLER_OFFER_ID1).orElseThrow();
        assertNotNull(existOffer);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").withZone(ZoneOffset.UTC);

        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        mockMvc.perform(get("/sellerOffer/{id}/user/{userId}", EXIST_SELLER_OFFER_ID1, EXISTING_ADMIN1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.offerStatus").value(existOffer.getOfferStatus().name()))
                .andExpect(jsonPath("$.comment").value(existOffer.getComment()))
                .andExpect(jsonPath("$.productId").value(existOffer.getProduct().getId()))
                .andExpect(jsonPath("$.addressId").value(existOffer.getAddress().getId()))
                .andExpect(jsonPath("$.userId").value(existOffer.getSeller().getId()))
                .andExpect(jsonPath("$.creationDateTime").value(existOffer.getCreationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andExpect(jsonPath("$.finishDateTime").value(existOffer.getFinishDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andExpect(jsonPath("$.createdAt").value(ZonedDateTime.ofInstant(existOffer.getCreatedAt(), ZoneOffset.UTC).format(formatter)));
    }

    @Test
    void getNotExistSellerOffer_ByAnybody_ShouldReturn404() throws Exception {
        long NOT_EXIST_SELLER_OFFER_ID = 37L;

        User prototype = TestDataUser.getUserPrototype(false);
        prototype = userRepository.save(prototype);
        assertNotNull(prototype);
        assertNotNull(prototype.getId());
        List<String> listRoles = prototype.getRoles().stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(prototype.getId(), listRoles, privateKey, keyId);

        mockMvc.perform(get("/sellerOffer/{id}/user/{userId}", NOT_EXIST_SELLER_OFFER_ID, prototype.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    void createSellerOffer_BySeller_ShouldReturn200() throws Exception {
        // GIVEN
        PreparedData result = getPreparedData();

        SellerOffer sellerOfferSample = SellerOffer.builder()
                .comment(faker.lorem().sentence(5))
                .offerStatus(result.offerStatus())
                .creationDateTime(result.creationDT())
                .finishDateTime(result.finishDT())
                .address(result.addressSample())
                .product(result.productSample())
                .seller(result.author())
                .createdAt(Instant.now())
                .build();

        // TEST

        List<String> listRoles = result.author().getRoles().stream().map(Enum::name).toList();
        String token = JwtTestUtils.generateBearerToken(result.author().getId(), listRoles, privateKey, keyId);

        SellerOfferCreateRequest requestSellerOffer = TestDataMapper.toSellerOfferCreateRequest(sellerOfferSample);
        assertNotNull(requestSellerOffer);

        String requestJson = toJson(requestSellerOffer);

        mockMvc.perform(post("/sellerOffer")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sellerId", String.valueOf(result.author().getId()))
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.offerStatus").value(requestSellerOffer.offerStatus().name()))
                .andExpect(jsonPath("$.creationDateTime").isString())
                .andExpect(jsonPath("$.creationDateTime").value(requestSellerOffer.creationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andExpect(jsonPath("$.finishDateTime").isString())
                .andExpect(jsonPath("$.finishDateTime").value(requestSellerOffer.finishedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andExpect(jsonPath("$.productId").value(requestSellerOffer.productId()))
                .andExpect(jsonPath("$.addressId").value(requestSellerOffer.addressId()))
                .andExpect(jsonPath("$.createdAt").isString())  // Проверяем, что это строка
                .andExpect(jsonPath("$.createdAt", matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}Z")))
                .andExpect(jsonPath("$.createdAt").value(endsWith("Z")))
                .andDo(print());
    }

    @Test
    void updateSellerOffer_ByAuthor_Should_Return200() throws Exception {
        // GIVEN
        PreparedData result = getPreparedData();

        SellerOffer sellerOfferSample = SellerOffer.builder()
                .comment(faker.lorem().sentence(5))
                .offerStatus(result.offerStatus())
                .creationDateTime(result.creationDT())
                .finishDateTime(result.finishDT())
                .address(result.addressSample())
                .product(result.productSample())
                .seller(result.author())
                .createdAt(Instant.now())
                .build();
        sellerOfferSample = sellerOfferRepository.save(sellerOfferSample);
        assertNotNull(sellerOfferSample);
        assertNotNull(sellerOfferSample.getId());

        // TEST

        List<String> listRoles = result.author().getRoles().stream().map(Enum::name).toList();
        String token = JwtTestUtils.generateBearerToken(result.author().getId(), listRoles, privateKey, keyId);

        SellerOfferCreateRequest updateRequest = TestDataMapper.toSellerOfferCreateRequest(sellerOfferSample);
        final OfferStatus currentStatus = updateRequest.offerStatus();
        updateRequest = updateRequest.toBuilder()
                .unitCount(updateRequest.unitCount() + 1)
                .offerStatus(Arrays.stream(OfferStatus.values())
                        .filter(offerStatus -> !offerStatus.equals(currentStatus))
                        .findFirst()
                        .orElseThrow())
                .comment(faker.lorem().sentence(10))
                .build();

        String updateRequestJson = toJson(updateRequest);

        MvcResult mvcResult = mockMvc.perform(put("/sellerOffer/{id}/user/{sellerId}", sellerOfferSample.getId(), result.author().getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sellerOfferSample.getId()))
                .andExpect(jsonPath("$.addressId").value(updateRequest.addressId()))
                .andExpect(jsonPath("$.creationDateTime").value(updateRequest.creationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andReturn();

        // ASSERTION
        String contentResponse = mvcResult.getResponse().getContentAsString();
        SellerOfferResponse sellerOfferResponse = toDto(contentResponse, SellerOfferResponse.class);
        assertNotNull(sellerOfferResponse);
        assertNotNull(sellerOfferResponse.id());
        assertEquals(updateRequest.offerStatus(), sellerOfferResponse.offerStatus());
        assertEquals(updateRequest.comment(), sellerOfferResponse.comment());
        assertEquals(updateRequest.unitCount(), sellerOfferResponse.partOfferToBuyResponseList().size());

    }


    @Test
    void deleteSellerOffer_ByAdmin_ShouldReturn200() throws Exception {
        //  GIVEN
        PreparedData preparedData = getPreparedData();

        SellerOffer sellerOfferToDelete = SellerOffer.builder()
                .comment(faker.lorem().sentence(5))
                .offerStatus(preparedData.offerStatus())
                .creationDateTime(preparedData.creationDT())
                .finishDateTime(preparedData.finishDT())
                .address(preparedData.addressSample())
                .product(preparedData.productSample())
                .seller(preparedData.author())
                .createdAt(Instant.now())
                .build();
        sellerOfferToDelete = sellerOfferRepository.save(sellerOfferToDelete);
        assertNotNull(sellerOfferToDelete);
        assertNotNull(sellerOfferToDelete.getId());

        // TEST

        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);

        mockMvc.perform(delete("/sellerOffer/{id}/user/{sellerId}", sellerOfferToDelete.getId(), EXISTING_ADMIN1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));

        // ASSERTION

        sellerOfferToDelete = sellerOfferRepository.findById(sellerOfferToDelete.getId()).orElse(null);
        assertNull(sellerOfferToDelete);
    }


    @Test
    void deleteSellerOffer_BySeller_ShouldReturn200() throws Exception {
        //GIVEN
        PreparedData preparedData = getPreparedData();

        SellerOffer sellerOfferToDelete = SellerOffer.builder()
                .comment(faker.lorem().sentence(5))
                .offerStatus(preparedData.offerStatus())
                .creationDateTime(preparedData.creationDT())
                .finishDateTime(preparedData.finishDT())
                .address(preparedData.addressSample())
                .product(preparedData.productSample())
                .seller(preparedData.author())
                .createdAt(Instant.now())
                .build();
        sellerOfferToDelete = sellerOfferRepository.save(sellerOfferToDelete);
        assertNotNull(sellerOfferToDelete);
        assertNotNull(sellerOfferToDelete.getId());

        // TEST

        List<String> listRoles = sellerOfferToDelete.getSeller().getRoles().stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(sellerOfferToDelete.getSeller().getId(), listRoles, privateKey, keyId);

        mockMvc.perform(delete("/sellerOffer/{id}/user/{sellerId}", sellerOfferToDelete.getId(), sellerOfferToDelete.getSeller().getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));

        // ASSERTION

        sellerOfferToDelete = sellerOfferRepository.findById(sellerOfferToDelete.getId()).orElse(null);
        assertNull(sellerOfferToDelete);
    }

    @Test
    void getAllMySellerOffer_BySeller_ShouldReturn200() throws Exception {
        User seller = userRepository.findById(USER1_ID).orElseThrow();
        assertNotNull(seller);
        List<String> listRoles = seller.getRoles().stream().map(Enum::name).toList();
        String token = JwtTestUtils.generateBearerToken(USER1_ID, listRoles, privateKey, keyId);
        mockMvc.perform(get("/sellerOffer/allMy")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sellerId", String.valueOf(USER1_ID)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllSellerOffer_ByAdmin_ShouldReturn200() throws Exception {
        long expectedSellerOfferId = EXIST_SELLER_OFFER_ID2;

        OfferStatus expectedStatus = OfferStatus.PRESALE;
        SellerOfferFilterRequest filterRequest = SellerOfferFilterRequest.builder()
                .userIds(Set.of(EXIST_SELLER_OFFER_ID1, expectedSellerOfferId, EXIST_SELLER_OFFER_ID3))
                .offerStatus(expectedStatus)
                .build();
        assertNotNull(filterRequest);
        String filterRequestJson = toJson(filterRequest);
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);

        MvcResult mvcResult = mockMvc.perform(post("/sellerOffer/all")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterRequestJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentResponse = mvcResult.getResponse().getContentAsString();
        Set<SellerOfferResponse> sellerOfferResponses = toDto(contentResponse, new TypeReference<>() {
        });
        assertNotNull(sellerOfferResponses);
        assertEquals(1, sellerOfferResponses.size());
        SellerOfferResponse sellerOffer = sellerOfferResponses.stream().findFirst().orElseThrow();
        assertNotNull(sellerOffer);
        assertEquals(expectedSellerOfferId, sellerOffer.id());
        assertEquals(expectedStatus, sellerOffer.offerStatus());
    }

}
