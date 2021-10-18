package com.epam.esm.controller.integration;

import com.epam.esm.WebApplication;
import com.epam.esm.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = WebApplication.class)
@TestPropertySource("classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerIntTest {

    private static final String CONTENT_TYPE = "application/json";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUsers() throws Exception {
        //given
        RequestBuilder request = MockMvcRequestBuilders.get("/users");

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andReturn();
    }

    @Test
    void getUserById() throws Exception {
        // given
        Integer id = 5;
        String userName = "misha";
        RequestBuilder request = MockMvcRequestBuilders.get("/users/{id}", id);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString(userName)))
                .andReturn();
    }

    @Test
    void getUserById_WithException() throws Exception {
        // given
        Integer id = 20; //there is no user with this id
        RequestBuilder request = MockMvcRequestBuilders.get("/users/{id}", id);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    void postOrder() throws Exception {
        //when
        Integer userId = 3;
        Integer giftId = 9;
        RequestBuilder request = MockMvcRequestBuilders.post("/users/{id}/orders", userId)
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(giftId));

        //then
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void getOrdersByUserId() throws Exception {
        //when
        Integer userId = 3;
        String giftName1 = "gift-upd777ate-000";
        String giftName2 = "testing2";
        RequestBuilder request = MockMvcRequestBuilders.get("/users/{id}/orders", userId);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString(giftName1)))
                .andExpect(content().string(containsString(giftName2)))
                .andReturn();
    }

    @Test
    void getCostAndDateOfBuyForUserByOrderId() throws Exception {
        //when
        Integer userId = 3;
        Integer orderId = 3;
        String cost = "639.4";
        String date = "2017-05-18";
        RequestBuilder request = MockMvcRequestBuilders.get("/users/{userId}/orders/{orderId}", userId, orderId);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString(cost)))
                .andExpect(content().string(containsString(date)))
                .andReturn();
    }
}