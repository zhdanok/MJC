package com.epam.esm.controller.integration;

import com.epam.esm.config.WebAppInitializer;
import com.epam.esm.config.WebMvcConfig;
import com.epam.esm.dto.GiftAndTagDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebMvcConfig.class, WebAppInitializer.class})
class GiftCertificateControllerIntTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BasicDataSource dataSource;

    private MockMvc mockMvc;


    private static final String CONTENT_TYPE = "application/json";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        ResourceDatabasePopulator tables = new ResourceDatabasePopulator();
        tables.addScript(new ClassPathResource("/drop.sql"));
        tables.addScript(new ClassPathResource("/table.sql"));
        tables.addScript(new ClassPathResource("/data.sql"));
        DatabasePopulatorUtils.execute(tables, dataSource);
    }

    @Test
    void getGiftCertificates() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts");
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andReturn();
    }


    @Test
    void getGiftCertificatesByTagName() throws Exception {
        String tagName = "house";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts/tag")
                .param("tag", tagName);
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("house")))
                .andReturn();
    }

    @Test
    void getGiftCertificatesBySubstr_ByName() throws Exception {
        String substr = "che";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts/substr")
                .param("substr", substr);
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("che")))
                .andReturn();
    }

    @Test
    void getGiftCertificatesBySubstr_ByDescription() throws Exception {
        String substr = "ript";
        RequestBuilder request = MockMvcRequestBuilders.get("/gifts/substr")
                .param("substr", substr);
        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE))
                .andExpect(content().string(containsString("ript")))
                .andReturn();

    }


    @Test
    void postCertificate() throws Exception {
        GiftAndTagDto dto = GiftAndTagDto.builder()
                .name("testing")
                .description("for test")
                .price(555.5)
                .duration(45)
                .tags(Arrays.asList())
                .build();

        RequestBuilder request = MockMvcRequestBuilders.post("/gifts")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void updateCertificates() throws Exception {
        Integer id = 3;
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "updatable");
        updates.put("price", 555.7);

        RequestBuilder request = MockMvcRequestBuilders.patch("/gifts/{id}", id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updates));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("resource updated")))
                .andReturn();

    }

    @Test
    void deleteCertificateById() throws Exception {
        Integer id = 5;
        RequestBuilder request = MockMvcRequestBuilders.delete("/gifts/{id}", id);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isAccepted())
                .andReturn();
    }
}