package com.epam.esm.controller.integration;

import com.epam.esm.WebApplication;
import com.epam.esm.dto.TagDto;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = WebApplication.class)
@TestPropertySource("classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TagControllerIntTest {

	private static final String CONTENT_TYPE = "application/json";

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getTags() throws Exception {
		// given
		RequestBuilder request = MockMvcRequestBuilders.get("/tags");

		// then
		mockMvc.perform(request).andExpect(status().isUnauthorized())
				.andReturn();
	}



	@Test
	void getTagById() throws Exception {
		// given
		Integer id = 1920;
		String userName = "aardvark";
		RequestBuilder request = MockMvcRequestBuilders.get("/tags/{id}", id);

		// then
		mockMvc.perform(request).andExpect(status().isUnauthorized())
				.andReturn();
	}

	@Test
	@Transactional
	void postTag() throws Exception {
		// when
		TagDto dto = TagDto.builder().name("newtag").build();
		RequestBuilder request = MockMvcRequestBuilders.post("/tags").contentType(CONTENT_TYPE)
				.content(objectMapper.writeValueAsString(dto));

		// then
		this.mockMvc.perform(request).andExpect(status().isForbidden()).andReturn();
	}

	@Test
	void deleteTagById() throws Exception {
		// when
		Integer id = 4488;
		RequestBuilder request = MockMvcRequestBuilders.delete("/tags/{id}", id);

		// then
		this.mockMvc.perform(request).andExpect(status().isForbidden()).andReturn();
	}

	@Test
	void getMostPopularTagOfUserWithHighestCostOfOrder() throws Exception {
		// given
		String tagName = "abrogation";
		RequestBuilder request = MockMvcRequestBuilders.get("/tags/pop");

		// then
		mockMvc.perform(request).andExpect(status().isUnauthorized())
				.andReturn();
	}

}
