package org.gafiev.peertopeerbazaar.controller;

import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataMapper;
import org.gafiev.peertopeerbazaar.common.TestDataProduct;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.repository.ProductRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.gafiev.peertopeerbazaar.common.TestDataProduct.EXIST_PRODUCT_ID;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.USER1_ID;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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

public class ProductControllerTest extends BaseIntegrationTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getExistProduct_ByExistUser_ShouldReturn200() throws Exception {
        String token = JwtTestUtils.generateBearerToken(USER1_ID, List.of("USER", "BUYER", "SELLER"), privateKey, keyId);
        mockMvc.perform(get("/product/{id}/user/{userId}", EXIST_PRODUCT_ID, USER1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(EXIST_PRODUCT_ID))
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getSampleProduct_ByAnybody_ShouldReturn200() throws Exception {
        User anybody = TestDataUser.getUserPrototype(true);
        anybody = userRepository.save(anybody);
        assertNotNull(anybody);
        assertNotNull(anybody.getId());

        Product productSample = TestDataProduct.getProductSample();
        productSample = productRepository.save(productSample);
        assertNotNull(productSample);
        assertNotNull(productSample.getId());

        List<String> roleList = anybody.getRoles().stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(anybody.getId(), roleList, privateKey, keyId);
        mockMvc.perform(get("/product/{id}/user/{userId}", productSample.getId(), anybody.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productSample.getId()));
    }

    @Test
    void getNotExistProduct_ByAnybody_ShouldReturn404() throws Exception {
        User anybody = TestDataUser.getUserPrototype(true);
        anybody = userRepository.save(anybody);
        assertNotNull(anybody);
        assertNotNull(anybody.getId());

        long notExistProductId = 999L;
        List<String> roleList = anybody.getRoles().stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(anybody.getId(), roleList, privateKey, keyId);
        mockMvc.perform(get("/product/{id}/user/{userId}", notExistProductId, anybody.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    void createProduct_ByUser_ShouldReturn200() throws Exception {
        User author = userRepository.findById(USER1_ID).orElseThrow();
        Product product = TestDataProduct.getProductSample();
        product.setAuthor(author);

        ProductCreateRequest request = TestDataMapper.toProductCreateRequest(product);

        String requestJson = toJson(request);
        List<String> roles = author.getRoles().stream().map(Enum::name).toList();
        String token = JwtTestUtils.generateBearerToken(USER1_ID, roles, privateKey, keyId);

        MvcResult mvcResult = mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(request.name())))
                .andExpect(jsonPath("$.description", is(request.description())))
                .andExpect(jsonPath("$.portionUnit", is(request.portionUnit().name())))
                .andExpect(jsonPath("$.category", is(request.category().name())))
                .andExpect(jsonPath("$.imageURI", is(request.imageURI())))
                .andExpect(jsonPath("$.qrCode", is(request.qrCode())))
                // Исправлено: используем closeTo для чисел с плавающей точкой (погрешность 0.01)
                .andExpect(jsonPath("$.weight", closeTo(request.weight().doubleValue(), 0.01)))
                .andExpect(jsonPath("$.volume", closeTo(request.volume().doubleValue(), 0.01)))
                .andExpect(jsonPath("$.price", closeTo(request.price().doubleValue(), 0.01)))
                .andDo(print())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ProductResponse response = toDto(responseContent, ProductResponse.class);
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(request.price(), response.price());
        assertEquals(request.weight(), response.weight());
        assertEquals(request.volume(), response.volume());
    }

    @Test
    void updateProduct_ByAuthor_ShouldReturn200() throws Exception {
        //Given
        Product productSample = TestDataProduct.getProductSample();
        User author = productSample.getAuthor();
        author = userRepository.save(author);
        assertNotNull(author);
        assertNotNull(author.getId());
        productSample.setAuthor(author);
        productSample = productRepository.save(productSample);
        assertNotNull(productSample);
        assertNotNull(productSample.getId());
        //Test
        ProductUpdateRequest updateRequest = TestDataMapper.toProductUpdateRequest(productSample);

        updateRequest = updateRequest.toBuilder()
                .description(faker.lorem().sentence(6))
                .name("New Test Name")
                .price(new BigDecimal("57889.45"))
                .build();
        String updateJson = toJson(updateRequest);

        List<String> listRoles = author.getRoles().stream().map(Enum::name).toList();
        String token = JwtTestUtils.generateBearerToken(author.getId(), listRoles, privateKey, keyId);

        MvcResult mvcResult = mockMvc.perform(put("/product/{id}", productSample.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(updateRequest.name())))
                .andExpect(jsonPath("$.description", is(updateRequest.description())))
                .andExpect(jsonPath("$.portionUnit", is(updateRequest.portionUnit().name())))
                .andExpect(jsonPath("$.category", is(updateRequest.category().name())))
                .andExpect(jsonPath("$.imageURI", is(updateRequest.imageURI())))
                .andExpect(jsonPath("$.qrCode", is(updateRequest.qrCode())))
                .andDo(print())
                .andReturn();
        //Assertion
        String responseContent = mvcResult.getResponse().getContentAsString();
        ProductResponse response = toDto(responseContent, ProductResponse.class);
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(updateRequest.description(), response.description());
        assertEquals(updateRequest.name(), response.name());
        assertEquals(updateRequest.price(), response.price());
        assertEquals(updateRequest.weight(), response.weight());
        assertEquals(updateRequest.volume(), response.volume());
    }

    @Test
    void getProductOfAuthor_ByAnybody_ShouldReturn200() throws Exception {
        User anybody = TestDataUser.getUserPrototype(true);
        anybody = userRepository.save(anybody);
        assertNotNull(anybody);
        assertNotNull(anybody.getId());
        List<String> listRoles = anybody.getRoles().stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(anybody.getId(), listRoles, privateKey, keyId);
        mockMvc.perform(get("/product/author/{authorId}",USER1_ID)
                        .param("authorId", String.valueOf(USER1_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void getAllProduct_ByAdmin_ShouldReturn200() throws Exception {
        ProductFilterRequest productFilter = TestDataProduct.getProductFilter();
        String filterJson = toJson(productFilter);
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        mockMvc.perform(post("/product/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

    }

    @Test
    void deleteProduct_ByAdmin_ShouldReturn200() throws Exception {
        Product productToDelete = TestDataProduct.getProductSample();
        productToDelete = productRepository.save(productToDelete);
        assertNotNull(productToDelete);
        assertNotNull(productToDelete.getId());
        long productId = productToDelete.getId();
        String admin = "ADMIN";
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of(admin), privateKey, keyId);
        mockMvc.perform(
                        delete("/product/{id}/author/{userId}", productId, EXISTING_ADMIN1_ID)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));

        productToDelete = productRepository.findById(productId).orElse(null);
        assertNull(productToDelete);
    }

    @Test
    void deleteProduct_ByAuthor_ShouldReturn200() throws Exception {
        Product productToDelete = TestDataProduct.getProductSample();
        productToDelete = productRepository.save(productToDelete);
        assertNotNull(productToDelete);
        assertNotNull(productToDelete.getId());

        User author = productToDelete.getAuthor();
        author = userRepository.save(author);
        assertNotNull(author);
        assertNotNull(author.getId());

        Set<Role> roles = author.getRoles();
        List<String> roleList = roles.stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(author.getId(), roleList, privateKey, keyId);
        mockMvc.perform(
                        delete("/product/{id}/author/{userId}", productToDelete.getId(), author.getId())
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));

        productToDelete = productRepository.findById(productToDelete.getId()).orElse(null);
        assertNull(productToDelete);
    }
}
