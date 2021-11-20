package com.epam.esm.repository;

import com.epam.esm.entity.UserProfile;
import com.epam.esm.entity.UsersOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    /**
     * Send request for getting all {@link UserProfile} with required page and limit
     *
     * @param skip  - count of page which need to skip
     * @param limit - count of Users which need to view at page
     * @return List of User with requirement parameters
     */
    List<UserProfile> findAll(Integer skip, Integer limit);

    /**
     * Send request for getting {@link UserProfile} by id
     *
     * @param id - Integer
     * @return Instance of User
     */
    UserProfile findById(Integer id);

    /**
     * Send request for saving {@link UsersOrder}
     */
    void save(UsersOrder usersOrder);

    /**
     * Send request for getting {@link UsersOrder}
     *
     * @param id    - Integer - User's id
     * @param skip  - count of page which need to skip
     * @param limit - count of Orders which need to view at page
     * @return List of UsersOrder
     */
    List<UsersOrder> findOrdersByUserId(Integer id, Integer skip, Integer limit);

    /**
     * Send request for getting {@link UsersOrder} for User by User id and Order id
     *
     * @param userId  - Integer - User's id
     * @param orderId - Integer - Order's id
     * @return UsersOrder
     */
    UsersOrder findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId);

    /**
     * Send request for getting count of all Users
     *
     * @return Long
     */
    Long findSize();

    /**
     * Send request for getting count of all {@link UsersOrder} for User by User's id
     *
     * @param userId - Integer - User's id
     * @return Long
     */
    Long findUsersOrdersSize(Integer userId);

    /**
     * Send request for saving {@link UserProfile}
     *
     * @param userProfile - Entity which need to save
     */
    void saveUser(UserProfile userProfile);

    /**
     * Send request for getting User's id by User's name
     *
     * @param name - User's name
     * @return User's id
     */
    Integer findUserIdByUserName(String name);

    UserProfile findByLogin(String login);

    Integer findIdByLogin(String login);
}
