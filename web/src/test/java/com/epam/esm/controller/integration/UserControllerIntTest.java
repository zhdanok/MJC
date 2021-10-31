package com.epam.esm.controller.integration;

import com.epam.esm.WebApplication;
import com.epam.esm.dto.UsersOrderDto;
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

	private static final String CONTENT_TYPE_HATEOAS = "application/hal+json";

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getUsers() throws Exception {
		// given
		RequestBuilder request = MockMvcRequestBuilders.get("/users");

		// then
		mockMvc.perform(request).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CONTENT_TYPE_HATEOAS)).andReturn();
	}

	@Test
	void getTags_isPaginationExist() throws Exception {
		// given
		Integer page = 2;
		Integer limit = 4;
		RequestBuilder request = MockMvcRequestBuilders.get("/users").param("page", String.valueOf(page)).param("limit",
				String.valueOf(limit));

		// then
		mockMvc.perform(request).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
				.andExpect(content().string(containsString("next"))).andExpect(content().string(containsString("last")))
				.andExpect(content().string(containsString("prev")))
				.andExpect(content().string(containsString("first"))).andReturn();
	}

	@Test
	void getTags_withInvalidPage() throws Exception {
		// given
		Integer page = 20;
		Integer limit = 5;
		RequestBuilder request = MockMvcRequestBuilders.get("/users").param("page", String.valueOf(page)).param("limit",
				String.valueOf(limit));

		// then
		mockMvc.perform(request).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().contentType(CONTENT_TYPE))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
				.andReturn();
	}

	@Test
	void getUserById() throws Exception {
		// given
		Integer id = 5;
		String userName = "Antonia Tramontano";
		RequestBuilder request = MockMvcRequestBuilders.get("/users/{id}", id);

		// then
		mockMvc.perform(request).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
				.andExpect(content().string(containsString(userName))).andReturn();
	}

	@Test
	void getUserById_WithException() throws Exception {
		// given
		Integer id = 40; // there is no user with this id
		RequestBuilder request = MockMvcRequestBuilders.get("/users/{id}", id);

		// then
		mockMvc.perform(request).andDo(print()).andExpect(status().isNotFound())
				.andExpect(content().contentType(CONTENT_TYPE))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
				.andReturn();
	}

	@Test
	void postOrder() throws Exception {
		// when
		Integer userId = 3;
		Integer giftId = 9;
		UsersOrderDto dto = UsersOrderDto.builder().giftId(giftId).build();
		RequestBuilder request = MockMvcRequestBuilders.post("/users/{id}/orders", userId).contentType(CONTENT_TYPE)
				.content(objectMapper.writeValueAsString(dto));

		// then
		this.mockMvc.perform(request).andDo(print()).andExpect(status().isCreated()).andReturn();
	}

	@Test
	void getOrdersByUserId() throws Exception {
		// when
		Integer userId = 10;
		String giftName1 = "rotogravure";
		String giftName2 = "sollerets";
		RequestBuilder request = MockMvcRequestBuilders.get("/users/{id}/orders", userId);

		// then
		mockMvc.perform(request).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
				.andExpect(content().string(containsString(giftName1)))
				.andExpect(content().string(containsString(giftName2))).andReturn();
	}

	@Test
	void getCostAndDateOfBuyForUserByOrderId() throws Exception {
		// when
		Integer userId = 10;
		Integer orderId = 25;
		String cost = "217.02308123154566";
		String date = "2021-10-31";
		RequestBuilder request = MockMvcRequestBuilders.get("/users/{userId}/orders/{orderId}", userId, orderId);

		// then
		mockMvc.perform(request).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CONTENT_TYPE_HATEOAS))
				.andExpect(content().string(containsString(cost))).andExpect(content().string(containsString(date)))
				.andReturn();
	}

}
