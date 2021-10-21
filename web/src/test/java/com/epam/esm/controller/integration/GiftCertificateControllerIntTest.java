package com.epam.esm.controller.integration;

import com.epam.esm.WebApplication;
import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.exception.BadRequestException;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
class GiftCertificateControllerIntTest {

    private static final String CONTENT_TYPE = "application/json";

    private static final String CONTENT_TYPE_HATEOAS = "application/hal+json";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getGiftCertificates() throws Exception {
        //given
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts");

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andReturn();
    }

    @Test
    void getGiftCertificateById() throws Exception {
        // given
        Integer id = 5;
        String nameOfGiftCertificate = "disc gift";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts/{id}", id);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString(nameOfGiftCertificate)))
                .andReturn();
    }

    @Test
    void getGiftCertificateById_WithException() throws Exception {
        // given
        Integer id = 20; //there is no certificate with this id
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts/{id}", id);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    void getGiftCertificatesByTagName() throws Exception {
        //when
        String[] tags = {"house", "cofe"};
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts")
                .param("tag", tags);

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString("house")))
                .andExpect(content().string(containsString("cofe")))
                .andReturn();
    }

    @Test
    void getGiftCertificatesBySubstr_ByName() throws Exception {
        //when
        String substr = "che";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts")
                .param("substr", substr);

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString("che")))
                .andReturn();
    }

    @Test
    void getGiftCertificatesBySubstr_ByDescription() throws Exception {
        //when
        String substr = "gift";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts")
                .param("substr", substr);

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString(substr)))
                .andReturn();
    }

    @Test
    void getGiftCertificatesByAnyParams() throws Exception {
        //when
        String tagName = "house";
        String substr = "gift";
        String sort = "asc";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts")
                .param("tag", tagName)
                .param("substr", substr)
                .param("sort", sort);

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString("house")))
                .andExpect(content().string(containsString("gift")))
                .andReturn();
    }

    @Test
    void getGiftCertificatesByAnyParams_WithException() throws Exception {
        //when
        String tagName = "tictok";
        String substr = "gift";
        String sort = "asc";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts")
                .param("tag", tagName)
                .param("substr", substr)
                .param("sort", sort);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    void postCertificate() throws Exception {
        //when
        GiftAndTagDto dto = GiftAndTagDto.builder()
                .name("testing2")
                .description("for test")
                .price(555.5)
                .duration(45)
                .tags(Collections.emptyList())
                .build();
        RequestBuilder request = MockMvcRequestBuilders.post("/gifts")
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(dto));

        //then
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void updateCertificates() throws Exception {
        //when
        Integer id = 3;
        Map<String, Object> updates = new HashMap<>();
        updates.put("gift_name", "updatable");
        updates.put("price", 555.7);
        RequestBuilder request = MockMvcRequestBuilders.patch("/gifts/{id}", id)
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(updates));

        //then
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("resource updated")))
                .andReturn();
    }

    @Test
    void updateCertificates_WithBadRequestException() throws Exception {
        //when
        Integer id = -3;
        Map<String, Object> updates = new HashMap<>();
        updates.put("gift_name", "update");
        updates.put("price", 555.7);
        RequestBuilder request = MockMvcRequestBuilders.patch("/gifts/{id}", id)
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(updates));

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    @Test
    void updateCertificates_WithNotFoundException() throws Exception {
        //when
        Integer id = 25;
        Map<String, Object> updates = new HashMap<>();
        updates.put("gift_name", "update");
        updates.put("price", 555.7);
        RequestBuilder request = MockMvcRequestBuilders.patch("/gifts/{id}", id)
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(updates));

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    void deleteCertificateById() throws Exception {
        //when
        Integer id = 16;
        RequestBuilder request = MockMvcRequestBuilders.delete("/gifts/{id}", id);

        //then
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    void deleteCertificates_WithBadRequestException() throws Exception {
        //when
        Integer id = -17;
        RequestBuilder request = MockMvcRequestBuilders.delete("/gifts/{id}", id);

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    @Test
    void deleteCertificates_WithNotFoundException() throws Exception {
        //when
        Integer id = 21;
        RequestBuilder request = MockMvcRequestBuilders.delete("/gifts/{id}", id);

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }
}
