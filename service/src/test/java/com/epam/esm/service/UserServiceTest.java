package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ServiceApplication.class)
class UserServiceTest {

	@Autowired
	UserService service;

	@Autowired
	Converter<User, UserDto> converter;

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