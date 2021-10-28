package com.epam.esm.repository;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.UsersOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    List<UserDto> findAll(Integer skip, Integer limit);

    List<UserDto> findById(Integer id);

    void save(UsersOrder usersOrder);

    List<OrderDto> findOrdersByUserId(Integer id);

    List<CostAndDateOfBuyDto> findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId);

    Integer findSize();
}
