package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.dto.UsersOrderDto;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.repository.GiftCertificateDao;
import com.epam.esm.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.util.checkUtil.checkForBadRequestException;
import static com.epam.esm.util.checkUtil.checkForNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserDao userDao;

	private final GiftCertificateDao giftCertificateDao;

	private final Converter<User, UserDto> converter;

	private final Converter<UsersOrder, UsersOrderDto> converterForOrder;

	/**
	 * Send request for getting all Users
	 * @return List of UserDto
	 */
	public List<UserDto> getUsers(Integer page, Integer limit) {
		checkForBadRequestException(page <= 0 || page > getLastPage(limit), String.format("Invalid page --> %d", page));
		checkForBadRequestException(limit <= 0, String.format("Invalid limit --> %d", page));
		Integer skip = (page - 1) * limit;
		List<User> list = userDao.findAll(skip, limit);
		checkForNotFoundException(list.isEmpty(), "Users not found");
		return list.stream().map(user -> converter.convertToDto(user)).collect(Collectors.toList());
	}

	private Integer getLastPage(Integer limit) {
		Long sizeOfList = userDao.findSize();
		return Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
	}

	/**
	 * Send request for getting User by id
	 * @param id - Integer
	 * @return Instance of User
	 */
	public UserDto getUserById(Integer id) {
		checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id));
		User user = userDao.findById(id);
		checkForNotFoundException(user == null, String.format("User with id '%d' not found", id));
		return converter.convertToDto(user);
	}

	/**
	 * Send request for saving Order for User by Id
	 *
	 * @param userId - User's id, who want to create order
	 * @param dto    - UsersOrderDto which need to save
	 */
	@Transactional
	public void save(Integer userId, UsersOrderDto dto) {
		Double cost = giftCertificateDao.findPriceById(dto.getGiftId());
		String giftName = giftCertificateDao.findNameById(dto.getGiftId());
		UsersOrder usersOrder = UsersOrder.builder().userId(userId).giftId(dto.getGiftId()).giftName(giftName)
				.cost(cost).dateOfBuy(Instant.now()).build();
		userDao.save(usersOrder);
	}

	@Transactional
	public void saveUser(UserDto dto) {
		User user = converter.convertToEntity(dto);
		Integer id = userDao.findUserIdByUserName(dto.getName());
		if (id == null) {
			userDao.saveUser(user);
		}
	}

	/**
	 * Send request for getting User's orders
	 *
	 * @param id - Integer - User's id
	 * @return List of OrderDto
	 */
	public List<UsersOrderDto> getOrdersByUserId(Integer id, Integer page, Integer limit) {
		checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id));
		checkForBadRequestException(page <= 0 || page > getLastPage(limit), String.format("Invalid page --> %d", page));
		checkForBadRequestException(limit <= 0, String.format("Invalid limit --> %d", page));
		Integer skip = (page - 1) * limit;
		User user = userDao.findById(id);
		checkForNotFoundException(user == null, String.format("User with id '%d' not found", id));
		List<UsersOrder> list = userDao.findOrdersByUserId(id, skip, limit);
		checkForNotFoundException(list.isEmpty(), String.format("Orders for User with id '%d' not found", id));
		return list.stream().map(usersOrder -> converterForOrder.convertToDto(usersOrder)).collect(Collectors.toList());
	}

	/**
	 * Send request for getting User's orders
	 * @param userId - Integer - User's id
	 * @param orderId - Integer - User's id
	 * @return OrderDto
	 */
	public CostAndDateOfBuyDto getCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
		checkForBadRequestException(userId <= 0, String.format("Invalid User's id --> %d", userId));
		checkForBadRequestException(orderId <= 0, String.format("Invalid Order's id --> %d", orderId));
		UsersOrder usersOrder = userDao.findCostAndDateOfBuyForUserByOrderId(userId, orderId);
		checkForNotFoundException(usersOrder == null,
				String.format("Order with User's id '%d' and Order's id '%d' not found", userId, orderId));
		CostAndDateOfBuyDto dto = CostAndDateOfBuyDto.builder().cost(usersOrder.getCost())
				.dateOfBuy(usersOrder.getDateOfBuy()).build();
		return dto;
	}

	public Long getSize() {
		return userDao.findSize();
	}

	public Long getUsersOrdersSize(Integer userId) {
		return userDao.findUsersOrdersSize(userId);
	}

}
