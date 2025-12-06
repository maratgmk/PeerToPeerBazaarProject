package org.gafiev.peertopeerbazaar.controller;


import org.gafiev.peertopeerbazaar.common.BaseIntegrationTest;
import org.gafiev.peertopeerbazaar.common.TestDataUser;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.UserUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.UserResponse;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.testutils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.gafiev.peertopeerbazaar.common.TestDataUser.EXISTING_ADMIN1_ID;
import static org.gafiev.peertopeerbazaar.common.TestDataUser.USER1_ID;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

public class UserControllerTest extends BaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void getSelfProfile_ShouldReturn200() throws Exception {
        String token = JwtTestUtils.generateBearerToken(USER1_ID, List.of("USER", "BUYER", "SELLER"), privateKey, keyId);
        mockMvc.perform(get("/user/{id}", USER1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(USER1_ID));
    }

    @Test
    void getAnotherProfile_ShouldReturn403() throws Exception {
        long otherId = USER1_ID + 1;
        String token = JwtTestUtils.generateBearerToken(USER1_ID, List.of("USER", "BUYER", "SELLER"), privateKey, keyId);
        mockMvc.perform(get("/user/{id}", otherId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string(emptyString()))
                .andDo(print());
    }

    @Test
    void getUserProfile_ByAdmin_ShouldReturn200() throws Exception {
        String admin = "ADMIN";
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of(admin), privateKey, keyId);
        mockMvc.perform(get("/user/{id}", USER1_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(USER1_ID))
                .andExpect(jsonPath("$.roles").value(not(hasValue(admin))));
    }

    @Test
    void getAllUsersProfile_ByAdmin_ShouldReturn200() throws Exception {
        UserFilterRequest userFilterRequest = TestDataUser.getUserFilter();
        String filterJson = toJson(userFilterRequest);
        String admin = "ADMIN";
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of(admin), privateKey, keyId);
        mockMvc.perform(post("/user/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].roles").value(not(hasItem(admin))))
                .andDo(print());
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,true"})
    public void updateUserByAdminOrUser_ShouldReturn200(boolean isAdmin, boolean isSeller) throws Exception {
        User user = TestDataUser.getUserPrototype(isSeller);
        user = testRepositoryHelper.saveUser(user);
        assertNotNull(user);
        assertNotNull(user.getId());

        long userId = isAdmin ? EXISTING_ADMIN1_ID : user.getId();
        List<String> roles = isAdmin ? List.of("ADMIN") : isSeller ? List.of("USER", "BUYER", "SELLER") : List.of("USER", "BUYER");
        String token = JwtTestUtils.generateBearerToken(userId, roles, privateKey, keyId);

        UserUpdateRequest userUpdate = new UserUpdateRequest("Red", "Green", "+102-347-999-1111");
        String userUpdateJson = toJson(userUpdate);

        MvcResult userResult = mockMvc.perform(put("/user/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String userResultJson = userResult.getResponse().getContentAsString();
        UserResponse userResponse = toDto(userResultJson, UserResponse.class);
        assertNotNull(userResponse);
        assertEquals(userId,userResponse.id());
        assertNotEquals(user.getPhone(),userResponse.phone());
        assertEquals("+102-347-999-1111",userResponse.phone());

    }


    @Test
    void deleteUser_ByAdmin_ShouldReturn200() throws Exception {
        User userToDelete = TestDataUser.getUserPrototype(false);
        userToDelete = userRepository.save(userToDelete);
        assertNotNull(userToDelete);
        assertNotNull(userToDelete.getId());

        long userId = userToDelete.getId();
        String admin = "ADMIN";
        String token = JwtTestUtils.generateBearerToken(EXISTING_ADMIN1_ID, List.of(admin), privateKey, keyId);
        mockMvc.perform(delete("/user/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(emptyString()));

        userToDelete = userRepository.findById(userId).orElse(null);
        assertNull(userToDelete);
    }
}

