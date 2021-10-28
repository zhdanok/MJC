package com.epam.esm.repository;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.mapper.CostAndDateOfBuyDtoRowMapper;
import com.epam.esm.mapper.OrderDtoRowMapper;
import com.epam.esm.mapper.UserDtoRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<UserDto> findAll(Integer skip, Integer limit) {
        String sql = "select * from user where user_id >= (select user_id from user order by user_id limit ?, 1)\n" +
                "                        order by user_id limit ?";
        return jdbcTemplate.query(sql, new UserDtoRowMapper(), skip, limit);
    }

    @Override
    public List<UserDto> findById(Integer id) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        return jdbcTemplate.query(sql, new UserDtoRowMapper(), id);
    }

    @Override
    public void save(UsersOrder usersOrder) {
        /*String sql = "INSERT INTO `order`(user_id, gift_id, cost, date_of_buy, gift_name) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, order.getUserId(), order.getGiftId(), order.getCost(), Timestamp.from(Instant.now()), order.getGiftName());*/

    }

    @Override
    public List<OrderDto> findOrdersByUserId(Integer id) {
        String sql = "SELECT o.* FROM users_order o WHERE user_id = ? ORDER BY order_id";
        return jdbcTemplate.query(sql, new OrderDtoRowMapper(), id);
    }

    @Override
    public List<CostAndDateOfBuyDto> findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
        String sql = "SELECT o.cost, o.date_of_buy FROM users_order o WHERE user_id = ? AND order_id = ?";
        return jdbcTemplate.query(sql, new CostAndDateOfBuyDtoRowMapper(), userId, orderId);
    }

    @Override
    public Integer findSize() {
        String sql = "SELECT COUNT(*) FROM user";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
