package com.epam.esm.repository;

import com.epam.esm.entity.User;
import com.epam.esm.entity.UsersOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

	List<User> findAll(Integer skip, Integer limit);

	User findById(Integer id);

    void save(UsersOrder usersOrder);

    List<UsersOrder> findOrdersByUserId(Integer id, Integer skip, Integer limit);

    UsersOrder findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId);

    Long findSize();

    Long findUsersOrdersSize(Integer userId);

    void saveUser(User user);

    Integer findUserIdByUserName(String name);

}
