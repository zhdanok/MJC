package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.dto.UsersOrderDto;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = ServiceApplication.class)
class UserServiceTest {

	@Autowired
	UserService service;

	@Autowired
	Converter<User, UserDto> converter;

	@MockBean
	UserDao userDao;

	@Test
	void getUsers() {
		// given
		List<User> mockList = getMockList();
		List<UserDto> expList = getExpList();
		Integer page = 2;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;

		// when
		when(userDao.findAll(skip, limit)).thenReturn(mockList);
		when(userDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		List<UserDto> actualList = service.getUsers(page, limit);

		// then
		assertEquals(expList, actualList);
	}

	@Test
	void getUsers_withInvalidPage() {
		// given
		List<User> mockList = getMockList();
		Integer page = 5;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;
		String expected = String.format("Invalid page --> %d", page);

		// when
		when(userDao.findAll(skip, limit)).thenReturn(mockList);
		when(userDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Exception ex = assertThrows(BadRequestException.class, () -> service.getUsers(page, limit));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getUsers_withInvalidLimit() {
		// given
		List<User> mockList = getMockList();
		Integer page = 1;
		Integer limit = -7;
		Integer skip = (page - 1) * limit;
		String expected = String.format("Invalid limit --> %d", limit);

		// when
		when(userDao.findAll(skip, limit)).thenReturn(mockList);
		when(userDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Exception ex = assertThrows(BadRequestException.class, () -> service.getUsers(page, limit));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getUsers_withNotFound() {
		// given
		List<User> mockList = getMockList();
		Integer page = 2;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;
		String expected = "Users not found";

		// when
		when(userDao.findAll(skip, limit)).thenReturn(Collections.EMPTY_LIST);
		when(userDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> service.getUsers(page, limit));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getUserById() {
		// given
		Integer id = 2;
		User mockUser = User.builder().userId(id).userName("User2").build();
		UserDto expUser = UserDto.builder().id(id).name("User2").build();

		// when
		when(userDao.findById(id)).thenReturn(mockUser);
		UserDto actual = service.getUserById(id);

		// then
		assertEquals(expUser, actual);
	}

	@Test
	void getUserById_withInvalidId() {
		// given
		Integer id = -7;
		String expected = String.format("Invalid id --> %d", id);

		// when
		Exception ex = assertThrows(BadRequestException.class, () -> service.getUserById(id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getUserById_withNotFound() {
		// given
		Integer id = 75;
		String expected = String.format("User with id '%d' not found", id);

		// when
		when(userDao.findById(id)).thenReturn(null);
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> service.getUserById(id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void saveUser() {
		// given
		Integer expId = 2;
		UserDto dto = UserDto.builder().name("User2").build();
		User mockUser = User.builder().userId(expId).userName("User2").build();

		// when
		when(userDao.findUserIdByUserName(mockUser.getUserName())).thenReturn(mockUser.getUserId());
		Integer actualId = service.saveUser(dto);

		// then
		assertEquals(expId, actualId);
	}

	@Test
	void getOrdersByUserId() {
		// given
		List<UsersOrder> mockList = getMockOrderList();
		List<UsersOrderDto> expList = getExpOrderList();
		User mockUser = User.builder().userId(1).userName("User1").build();
		Integer userId = 1;
		Integer page = 2;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;

		// when
		when(userDao.findById(userId)).thenReturn(mockUser);
		when(userDao.findOrdersByUserId(userId, skip, limit)).thenReturn(mockList);
		when(userDao.findUsersOrdersSize(userId)).thenReturn(Long.valueOf(mockList.size()));
		List<UsersOrderDto> actualList = service.getOrdersByUserId(userId, page, limit);

		// then
		assertEquals(expList, actualList);
	}

	@Test
	void getOrdersByUserId_withUserNotFound() {
		// given
		List<UsersOrder> mockList = getMockOrderList();
		User mockUser = User.builder().userId(1).userName("User1").build();
		Integer userId = 1;
		Integer page = 2;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;
		String expected = String.format("Orders for User with id '%d' not found", userId);

		// when
		when(userDao.findById(userId)).thenReturn(mockUser);
		when(userDao.findOrdersByUserId(userId, skip, limit)).thenReturn(Collections.EMPTY_LIST);
		when(userDao.findUsersOrdersSize(userId)).thenReturn(Long.valueOf(mockList.size()));
		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> service.getOrdersByUserId(userId, page, limit));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getCostAndDateOfBuyForUserByOrderId() {
		// given
		Integer userId = 1;
		Integer orderId = 1;
		UsersOrder mockOrder = UsersOrder.builder().orderId(orderId).userId(userId).giftId(1).cost(123.3).build();
		CostAndDateOfBuyDto expected = CostAndDateOfBuyDto.builder().cost(mockOrder.getCost()).build();

		// when
		when(userDao.findCostAndDateOfBuyForUserByOrderId(userId, orderId)).thenReturn(mockOrder);
		CostAndDateOfBuyDto actual = service.getCostAndDateOfBuyForUserByOrderId(userId, orderId);

		// then
		assertEquals(expected, actual);
	}

	@Test
	void getCostAndDateOfBuyForUserByOrderId_withNotFound() {
		// given
		Integer userId = 1;
		Integer orderId = 1;
		String expected = String.format("Order with User's id '%d' and Order's id '%d' not found", userId, orderId);

		// when
		when(userDao.findCostAndDateOfBuyForUserByOrderId(userId, orderId)).thenReturn(null);
		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> service.getCostAndDateOfBuyForUserByOrderId(userId, orderId));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getLastPageForUser() {
		// given
		Integer limit = 2;
		List<User> mockList = getMockList();

		// when
		when(userDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Integer actual = service.getLastPageForUser(limit);

		// then
		assertEquals(2, actual);
	}

	@Test
	void getLastPageForOrder() {
		// given
		Integer limit = 2;
		Integer userId = 1;
		List<UsersOrder> mockList = getMockOrderList();

		// when
		when(userDao.findUsersOrdersSize(userId)).thenReturn(Long.valueOf(
				mockList.stream().filter(s -> s.getUserId().equals(userId)).collect(Collectors.toList()).size()));
		Integer actual = service.getLastPageForOrder(userId, limit);

		// then
		assertEquals(2, actual);
	}

	private List<User> getMockList() {
		List<User> mockList = new ArrayList<>();
		mockList.add(User.builder().userId(1).userName("User1").build());
		mockList.add(User.builder().userId(2).userName("User2").build());
		mockList.add(User.builder().userId(3).userName("User3").build());
		return mockList;
	}

	private List<UserDto> getExpList() {
		List<UserDto> expList = new ArrayList<>();
		expList.add(UserDto.builder().id(1).name("User1").build());
		expList.add(UserDto.builder().id(2).name("User2").build());
		expList.add(UserDto.builder().id(3).name("User3").build());
		return expList;
	}

	private List<UsersOrder> getMockOrderList() {
		List<UsersOrder> mockList = new ArrayList<>();
		mockList.add(UsersOrder.builder().orderId(1).userId(1).giftId(1).build());
		mockList.add(UsersOrder.builder().orderId(2).userId(1).giftId(2).build());
		mockList.add(UsersOrder.builder().orderId(3).userId(1).giftId(3).build());

		return mockList;
	}

	private List<UsersOrderDto> getExpOrderList() {
		List<UsersOrderDto> expList = new ArrayList<>();
		expList.add(UsersOrderDto.builder().orderId(1).userId(1).giftId(1).build());
		expList.add(UsersOrderDto.builder().orderId(2).userId(1).giftId(2).build());
		expList.add(UsersOrderDto.builder().orderId(3).userId(1).giftId(3).build());
		return expList;
	}

	/**
	 * This test can generate 1300 new Users and save it to database. Please uncomment it
	 * only if you need to generate new data
	 */
	/*
	 * @Test void loadDataToTableUser() { MockNeat mockNeat = MockNeat.threadLocal();
	 *
	 * for (int i = 0; i < 1300; i++) { MockUnit<User> rUserGenerator =
	 * reflect(User.class).field("userName", mockNeat.names());
	 * service.saveUser(converter.convertToDto(rUserGenerator.get())); } }
	 */

	/**
	 * This test can generate 5000 new User's Orders and save it to database. Please
	 * uncomment it only if you need to generate new data
	 */
	/*
	 * @Test void loadDataToTableOrder() { MockNeat mockNeat = MockNeat.threadLocal();
	 *
	 * for (int i = 0; i < 5000; i++) { MockUnit<UsersOrderDto> rUserOrderGenerator =
	 * reflect(UsersOrderDto.class).field("giftId", mockNeat.ints().range(1, 10011));
	 *
	 * Integer userId = mockNeat.ints().range(1, 1300).get(); service.save(userId,
	 * rUserOrderGenerator.get()); } }
	 */

}