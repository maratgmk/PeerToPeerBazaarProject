package org.gafiev.peertopeerbazaar.controller;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.PartOfferToBuyResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuyStatus;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BasketControllerTest extends BaseIntegrationTest {
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getBasket_ByAdminOrBasketOwner_ShouldReturn200(boolean isAdmin) throws Exception {
        // GIVEN
        User user = createUserWithBasket();

        List<String> roles = isAdmin ? List.of("ADMIN") : List.of("USER", "BUYER");
        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        // TEST
        mockMvc.perform(get("/basket/{userId}", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.id").value(user.getBasket().getId()));
    }

    @Test
    void addPartOfferToBuy_ByBuyer_ShouldReturn200() throws Exception {
        // GIVEN
        int partCount = 3;
        int putToBasketCount = 1;
        SellerOffer sellerOfferSample = createSellerOffer(partCount);
        User buyer = createUserWithBasket();

            List<String> listRoles = List.of("ADMIN", "BUYER");
        String token = JwtTestUtils.generateBearerToken(buyer.getId(), listRoles, privateKey, keyId);

        // TEST
        MvcResult result = mockMvc.perform(post("/basket/{id}/add", buyer.getBasket().getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sellerOfferId", String.valueOf(sellerOfferSample.getId()))
                        .param("unitCount", String.valueOf(putToBasketCount)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        BasketResponse basketResponse = toDto(json, BasketResponse.class);
        assertNotNull(basketResponse);
        assertEquals(buyer.getBasket().getId(), basketResponse.id());
        assertFalse(basketResponse.parts().isEmpty());
        List<Long> partIds = sellerOfferSample.getPartOfferToBuyList().stream().map(PartOfferToBuy::getId).toList();
        assertTrue(basketResponse.parts().stream()
                .map(PartOfferToBuyResponse::id)
                .anyMatch(partIds::contains));
    }

    @Test
    @Transactional  // Выполняются три последовательных сохранения в БД
    void removePartOfferToBuy_ByBuyer_ShouldReturn200() throws Exception {
        // GIVEN
        int partCount = 3;
        SellerOffer sellerOfferSample = createSellerOffer(partCount);
        PartOfferToBuy partToRemove = sellerOfferSample.getPartOfferToBuyList().stream().findFirst().orElseThrow();

        User buyer = createUserWithBasketWithParts(List.of(partToRemove));

        List<String> listRoles = List.of("ADMIN", "BUYER");
        String token = JwtTestUtils.generateBearerToken(buyer.getId(), listRoles, privateKey, keyId);

        // Test
        MvcResult removeResult = mockMvc.perform(post("/basket/{id}/remove", buyer.getBasket().getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("partOfferToBuyId", String.valueOf(partToRemove.getId())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String removeResultJson = removeResult.getResponse().getContentAsString();
        BasketResponse responseBasket = toDto(removeResultJson, BasketResponse.class);
        assertNotNull(responseBasket);
        assertEquals(buyer.getBasket().getId(), responseBasket.id());
        assertTrue(responseBasket.parts().isEmpty());
    }

    @Nonnull
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
        assertNotNull(basket);
        assertFalse(basket.getPartOfferToBuySet().isEmpty());
        return basket;
    }

    @Nonnull
    private User createUserWithBasket() {
        return createUserWithBasketWithParts(null);
    }

    private User createUserWithBasketWithParts(@Nullable List<PartOfferToBuy> parts){
        User prototype = TestDataUser.getUserPrototype(false);
        Basket basket = new Basket();
        parts = Objects.requireNonNullElse(parts,new ArrayList<>());

        for (PartOfferToBuy part : parts) {
            basket.addPartOfferToBuy(part);
        }

        basket.setBuyer(prototype);
        prototype.setBasket(basket);
        prototype = testRepositoryHelper.saveUser(prototype);

        assertNotNull(prototype);
        assertNotNull(prototype.getId());

        prototype = testRepositoryHelper.findByIdWithBasket(prototype.getId());
        basket = prototype.getBasket();

        assertNotNull(basket);
        assertNotNull(basket.getId());
        return prototype;
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
}
