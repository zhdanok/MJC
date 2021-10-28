package com.epam.esm.controller.integration;

import com.epam.esm.WebApplication;
import com.epam.esm.dto.TagDto;
import com.epam.esm.exception.BadRequestException;
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
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = WebApplication.class)
@TestPropertySource("classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TagControllerIntTest {

    private static final String CONTENT_TYPE = "application/json";

    private static final String CONTENT_TYPE_HATEOAS = "application/hal+json";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTags() throws Exception {
        //given
        RequestBuilder request = MockMvcRequestBuilders.get("/tags");

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andReturn();
    }

    @Test
    void getTags_isPaginationExist() throws Exception {
        //given
        Integer page = 2;
        Integer limit = 4;
        RequestBuilder request = MockMvcRequestBuilders.get("/tags")
                .param("page", String.valueOf(page))
                .param("limit", String.valueOf(limit));

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString("next")))
                .andExpect(content().string(containsString("last")))
                .andExpect(content().string(containsString("prev")))
                .andExpect(content().string(containsString("first")))
                .andReturn();
    }

    @Test
    void getTags_withInvalidPage() throws Exception {
        //given
        Integer page = 20;
        Integer limit = 5;
        RequestBuilder request = MockMvcRequestBuilders.get("/tags")
                .param("page", String.valueOf(page))
                .param("limit", String.valueOf(limit));

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    @Test
    void getTagById() throws Exception {
        // given
        Integer id = 5;
        String userName = "makeup";
        RequestBuilder request = MockMvcRequestBuilders.get("/tags/{id}", id);

        // then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString(userName)))
                .andReturn();
    }

    @Test
    @Transactional
    void postTag() throws Exception {
        //when
        TagDto dto = TagDto.builder()
                .name("newtag")
                .build();
        RequestBuilder request = MockMvcRequestBuilders.post("/tags")
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(dto));

        //then
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void deleteTagById() throws Exception {
        //when
        Integer id = 23;
        RequestBuilder request = MockMvcRequestBuilders.delete("/tags/{id}", id);

        //then
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    void getMostPopularTagOfUserWithHighestCostOfOrder() throws Exception {
        //given
        String tagName = "house";
        RequestBuilder request = MockMvcRequestBuilders.get("/tags/pop");

        //then
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
                .andExpect(content().string(containsString(tagName)))
                .andReturn();
    }
}
