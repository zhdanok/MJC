package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.Order;
import com.epam.esm.repository.GiftCertificateDao;
import com.epam.esm.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.epam.esm.util.checkUtil.checkForBadRequestException;
import static com.epam.esm.util.checkUtil.checkForNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final GiftCertificateDao giftCertificateDao;

    private final Converter<Order, OrderDto> converter;

    /**
     * Send request for getting all Users
     *
     * @return List of UserDto
     */
    public List<UserDto> getUsers() {
        List<UserDto> list = userDao.findAll();
        checkForNotFoundException(list.isEmpty(), "Users not found");
        return list;
    }

    /**
     * Send request for getting User by id
     *
     * @param id - Integer
     * @return List of UserDto
     */
    public List<UserDto> getUserById(Integer id) {
        checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id));
        List<UserDto> list = userDao.findById(id);
        checkForNotFoundException(list.isEmpty(), String.format("User with id '%d' not found", id));
        return list;
    }

    /**
     * Send request for saving Order for User by Id
     *
     * @param userId - User's id, who want to create order
     * @param giftId - Gift's id, which User want to buy
     */
    @Transactional
    public void save(Integer userId, Integer giftId) {
        Double cost = giftCertificateDao.findPriceById(giftId);
        String giftName = giftCertificateDao.findNameById(giftId);
        Order order = Order.builder()
                .cost(cost)
                .userId(userId)
                .giftId(giftId)
                .giftName(giftName)
                .build();
        userDao.save(order);
    }

    /**
     * Send request for getting User's orders
     *
     * @param id - Integer - User's id
     * @return List of OrderDto
     */
    public List<OrderDto> getOrdersByUserId(Integer id) {
        checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id));
        List<UserDto> listOfUsers = userDao.findById(id);
        checkForNotFoundException(listOfUsers.isEmpty(), String.format("User with id '%d' not found", id));
        List<OrderDto> list = userDao.findOrdersByUserId(id);
        checkForNotFoundException(list.isEmpty(), String.format("Orders for User with id '%d' not found", id));
        return list;
    }

    /**
     * Send request for getting User's orders
     *
     * @param userId  - Integer - User's id
     * @param orderId - Integer - User's id
     * @return List of OrderDto
     */
    public List<CostAndDateOfBuyDto> getCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
        checkForBadRequestException(userId <= 0, String.format("Invalid User's id --> %d", userId));
        checkForBadRequestException(orderId <= 0, String.format("Invalid Order's id --> %d", orderId));
        List<CostAndDateOfBuyDto> list = userDao.findCostAndDateOfBuyForUserByOrderId(userId, orderId);
        checkForNotFoundException(list.isEmpty(), String.format("Order with User's id '%d' and Order's id '%d' not found", userId, orderId));
        return list;
    }
}
