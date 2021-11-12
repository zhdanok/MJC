package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.dto.UsersOrderDto;
import com.epam.esm.entity.UserProfile;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.repository.GiftCertificateDao;
import com.epam.esm.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.util.ExceptionUtils.*;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserDao userDao;

	private final GiftCertificateDao giftCertificateDao;

	private final Converter<UserProfile, UserDto> converter;

	private final Converter<UsersOrder, UsersOrderDto> converterForOrder;

	/**
	 * Send request for getting all UserDtos with required page and limit
	 *
	 * @param page  - number of page with required limit
	 * @param limit - count of Users which need to view at page
	 * @return List of UserDto with requirement parameters
	 */
	public List<UserDto> getUsers(Integer page, Integer limit) {
		checkForBadRequestException(page <= 0 || page > getLastPageForUser(limit),
				String.format("Invalid page --> %d", page), ERR_CODE_USER);
		checkForBadRequestException(limit <= 0, String.format("Invalid limit --> %d", limit), ERR_CODE_USER);
		Integer skip = (page - 1) * limit;
		List<UserProfile> list = userDao.findAll(skip, limit);
		checkForNotFoundException(list.isEmpty(), "Users not found", ERR_CODE_USER);
		return list.stream().map(converter::convertToDto).collect(Collectors.toList());
	}

	/**
	 * Send request for getting number of last Page for User with required limit
	 * @param limit - count of Users which need to view at page
	 * @return Long - number of last page
	 */
	public Integer getLastPageForUser(Integer limit) {
		Long sizeOfList = userDao.findSize();
		return Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
	}

	/**
	 * Send request for getting number of last Page for UsersOrder with required limit
	 * @param limit - count of Users which need to view at page
	 * @return Long - number of last page
	 */
	public Integer getLastPageForOrder(Integer userId, Integer limit) {
		Long sizeOfList = userDao.findUsersOrdersSize(userId);
		return Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
	}

	/**
	 * Send request for getting UserDto by id
	 * @param id - Integer
	 * @return UserDto
	 */
	public UserDto getUserById(Integer id) {
		checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id), ERR_CODE_USER);
		UserProfile userProfile = userDao.findById(id);
		checkForNotFoundException(userProfile == null, String.format("User with id '%d' not found", id), ERR_CODE_USER);
		return converter.convertToDto(userProfile);
	}

	/**
	 * Send request for saving Order for User by Id
	 * @param userId - User's id, who want to create order
	 * @param dto - UsersOrderDto which need to save
	 */
	@Transactional
	public void save(Integer userId, UsersOrderDto dto) {
		Double cost = giftCertificateDao.findPriceById(dto.getGiftId());
		String giftName = giftCertificateDao.findNameById(dto.getGiftId());
		UsersOrder usersOrder = UsersOrder.builder().userId(userId).giftId(dto.getGiftId()).giftName(giftName)
				.cost(cost).dateOfBuy(Instant.now()).build();
		userDao.save(usersOrder);
	}

	/**
	 * Send request for saving User
	 * @param dto - Dto of Entity which need to save
	 * @return Integer - id of new User (or of existed User if it existed)
	 */
	@Transactional
	public Integer saveUser(UserDto dto) {
		UserProfile userProfile = converter.convertToEntity(dto);
		Integer id = userDao.findUserIdByUserName(dto.getName());
		if (id == null) {
			userDao.saveUser(userProfile);
			return userDao.findUserIdByUserName(dto.getName());
		}
		return id;
	}

	/**
	 * Send request for getting User's orders
	 * @param id - Integer - User's id
	 * @return List of OrderDto
	 */
	public List<UsersOrderDto> getOrdersByUserId(Integer id, Integer page, Integer limit) {
		checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id), ERR_CODE_ORDER);
		checkForBadRequestException(page <= 0 || page > getLastPageForOrder(id, limit),
				String.format("Invalid page --> %d", page), ERR_CODE_ORDER);
		checkForBadRequestException(limit <= 0, String.format("Invalid limit --> %d", limit), ERR_CODE_ORDER);
		Integer skip = (page - 1) * limit;
		UserProfile userProfile = userDao.findById(id);
		checkForNotFoundException(userProfile == null, String.format("User with id '%d' not found", id), ERR_CODE_USER);
		List<UsersOrder> list = userDao.findOrdersByUserId(id, skip, limit);
		checkForNotFoundException(list.isEmpty(), String.format("Orders for User with id '%d' not found", id),
				ERR_CODE_ORDER);
		return list.stream().map(converterForOrder::convertToDto).collect(Collectors.toList());
	}

	/**
	 * Send request for getting Cost and Date of buy for User's order by Order id
	 * @param userId - Integer - User's id
	 * @param orderId - Integer - Order's id
	 * @return CostAndDateOfBuyDto
	 */
	public CostAndDateOfBuyDto getCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
		checkForBadRequestException(userId <= 0, String.format("Invalid User's id --> %d", userId), ERR_CODE_USER);
		checkForBadRequestException(orderId <= 0, String.format("Invalid Order's id --> %d", orderId), ERR_CODE_ORDER);
		UsersOrder usersOrder = userDao.findCostAndDateOfBuyForUserByOrderId(userId, orderId);
		checkForNotFoundException(usersOrder == null,
				String.format("Order with User's id '%d' and Order's id '%d' not found", userId, orderId),
				ERR_CODE_ORDER);
		CostAndDateOfBuyDto dto = CostAndDateOfBuyDto.builder().cost(usersOrder.getCost())
				.dateOfBuy(usersOrder.getDateOfBuy()).build();
		return dto;
	}

	/**
	 * Send request for getting count of all Users
	 * @return Long
	 */
	public Long getSize() {
		return userDao.findSize();
	}

	/**
	 * Send request for getting count of all Orders for User by User's id
	 * @param userId - Integer - User's id
	 * @return Long
	 */
	public Long getUsersOrdersSize(Integer userId) {
		return userDao.findUsersOrdersSize(userId);
	}

}
