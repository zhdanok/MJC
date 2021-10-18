package com.epam.esm.repository;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    List<UserDto> findAll();

    List<UserDto> findById(Integer id);

    void save(Order order);

    List<OrderDto> findOrdersByUserId(Integer id);

    List<CostAndDateOfBuyDto> findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId);
}
