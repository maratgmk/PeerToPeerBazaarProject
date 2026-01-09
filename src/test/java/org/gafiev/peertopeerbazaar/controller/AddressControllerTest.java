package org.gafiev.peertopeerbazaar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataAddress;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.AddressResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.properties.DroneProperties;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static org.gafiev.peertopeerbazaar.common.TestDataAddress.EXIST_ADDRESS_ID;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.USER1_ID;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

public class AddressControllerTest extends BaseIntegrationTest {
    @Autowired
    private DroneProperties droneProperties;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getExistAddress_ByAdmin_ShouldReturn200() throws Exception {
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);

        mockMvc.perform(get("/address/{id}/user/{userId}", EXIST_ADDRESS_ID, EXISTING_ADMIN1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(EXIST_ADDRESS_ID));
    }

    @Test
    void getExistAddress_ByUser_ShouldReturn200() throws Exception {
        String token = JwtTestUtils.generateBearerToken(USER1_ID, List.of("USER", "BUYER", "SELLER"), privateKey, keyId);

        mockMvc.perform(get("/address/{id}/user/{userId}", EXIST_ADDRESS_ID, USER1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(EXIST_ADDRESS_ID))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getAddressSample_byAdmin_ShouldReturn200() throws Exception {
        Address addressSample = TestDataAddress.getAddressSample();
        addressSample = addressRepository.save(addressSample);
        assertNotNull(addressSample);
        assertNotNull(addressSample.getId());

        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        mockMvc.perform(get("/address/{id}/user/{userId}", addressSample.getId(), EXISTING_ADMIN1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressSample.getId()))
                .andExpect(jsonPath("$.longitude").exists())
                .andExpect(jsonPath("$.longitude").value(addressSample.getLongitude()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt")
                        .value(addressSample.getCreatedAt().getEpochSecond() + "." + addressSample.getCreatedAt().getNano()));

    }

    @Test
    void getAddressSample_byAnybody_ShouldReturn200() throws Exception {
        Address addressSample = TestDataAddress.getAddressSample();
        addressSample = addressRepository.save(addressSample);
        assertNotNull(addressSample);
        assertNotNull(addressSample.getId());

        User anybody = TestDataUser.getUserPrototype(false);
        anybody = userRepository.save(anybody);
        assertNotNull(anybody);
        assertNotNull(anybody.getId());

        List<String> listRoles = anybody.getRoles().stream().map(Enum::name).toList();

        String token = JwtTestUtils.generateBearerToken(anybody.getId(), listRoles, privateKey, keyId);
        mockMvc.perform(get("/address/{id}/user/{userId}", addressSample.getId(), anybody.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(addressSample.getId()))
                .andExpect(jsonPath("$.attitude").exists())
                .andExpect(jsonPath("$.attitude").value(addressSample.getAltitude()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt")
                        .value(addressSample.getCreatedAt().getEpochSecond() + "." + addressSample.getCreatedAt().getNano()));
    }

    @Test
    void getAllMyAddress_ByUser_ShouldReturn200() throws Exception {
        String token = JwtTestUtils.generateBearerToken(USER1_ID, List.of("USER", "BUYER", "SELLER"), privateKey, keyId);

        MvcResult result = mockMvc.perform(get("/address/user")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(USER1_ID)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String resultJson = result.getResponse().getContentAsString();
        Set<AddressResponse> addresses = toDto(resultJson, new TypeReference<Set<AddressResponse>>() {
        });
        assertNotNull(addresses);
        assertEquals(5, addresses.size());
    }

    @Test
    void getAllAddress_ByAdmin_ShouldReturn200() throws Exception {
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of("ADMIN"), privateKey, keyId);
        AddressFilterRequest filterRequest = TestDataAddress.getAAddressFilterRequest();
        String filterRequestJson = toJson(filterRequest);

        MvcResult result = mockMvc.perform(post("/address/filter")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(filterRequestJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andReturn();
        String resultJson = result.getResponse().getContentAsString();
        Set<AddressResponse> addresses = toDto(resultJson, new TypeReference<Set<AddressResponse>>() {
        });
        assertNotNull(addresses);
        assertEquals(2, addresses.size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void createAddressByUser_ShouldReturn200(boolean isSeller) throws Exception {
        User user = TestDataUser.getUserPrototype(isSeller);
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());
        List<String> roles = isSeller ? List.of("USER", "BUYER", "SELLER") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(user.getId(), roles, privateKey, keyId);

        AddressCreateRequest createRequest = TestDataAddress.addressCreateRequest();
        String createRequestJson = toJson(createRequest);

        stubForAddressCheck(createRequest);

        MvcResult result = mockMvc.perform(post("/address/create/user/{userId}", user.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        AddressResponse addressResponse = toDto(resultJson, AddressResponse.class);
        assertNotNull(addressResponse);
        assertNotNull(addressResponse.id());
        assertEquals(createRequest.town(), addressResponse.town());
    }

    @SneakyThrows
    private void stubForAddressCheck(AddressCreateRequest createRequest) {
        String createRequestJson = toJson(createRequest);
        String path = new URI(droneProperties.getClientUri()).getPath() + "/code";
        DRONE_OPERATOR.stubFor(WireMock.post(WireMock.urlPathEqualTo(path))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalToIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(createRequestJson))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("allowed")));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updateAddressByUser_ShouldReturn200(boolean isSeller) throws Exception {
        User user = TestDataUser.getUserPrototype(isSeller);
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());
        List<String> roles = isSeller ? List.of("USER", "BUYER", "SELLER") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(user.getId(), roles, privateKey, keyId);

        Address address = TestDataAddress.getAddressSample();
        address = testRepositoryHelper.saveAddress(address);
        assertNotNull(address);
        assertNotNull(address.getId());

        AddressCreateRequest addressNew = TestDataAddress.addressCreateRequest();
        String addressNewJson = toJson(addressNew);

        stubForNewAddressCheck(addressNew);

        MvcResult result = mockMvc.perform(put("/address/update/{id}/user/{userId}", address.getId(), user.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addressNewJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        AddressResponse addressResponse = toDto(resultJson, AddressResponse.class);
        assertNotNull(addressResponse);
        assertEquals(address.getId(), addressResponse.id());
        assertNotEquals(address.getTown(), addressResponse.town());
    }

    @SneakyThrows
    private void stubForNewAddressCheck(AddressCreateRequest addressNew) {
        String addressNewJson = toJson(addressNew);
        String path = new URI(droneProperties.getClientUri()).getPath() + "/code";
        DRONE_OPERATOR.stubFor(WireMock.post(WireMock.urlPathEqualTo(path))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalToIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(addressNewJson))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("allowed")));
    }

    @ParameterizedTest
    @CsvSource({"true,true","true,false","false,true","false,false"})
    void deleteAddressByAdmin_ShouldReturn200(boolean isAdmin, boolean isSeller) throws Exception {
        User user = TestDataUser.getUserPrototype(isSeller);
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());
        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : isSeller ? List.of("USER","BUYER","SELLER") : List.of("USER","BUYER");
        String token = JwtTestUtils.generateBearerToken(userId,roles,privateKey,keyId);

        Address addressToDelete = TestDataAddress.getAddressSample();
        addressToDelete = testRepositoryHelper.saveAddress(addressToDelete);
        assertNotNull(addressToDelete);
        assertNotNull(addressToDelete.getId());
        long addressToDeleteId = addressToDelete.getId();

        mockMvc.perform(delete("/address/{id}/user/{userId}",addressToDeleteId,userId)
                .header(HttpHeaders.AUTHORIZATION,BEARER_PREFIX + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));
        assertTrue(testRepositoryHelper.findAddressById(addressToDeleteId).isEmpty());
    }
}
