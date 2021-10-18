package com.epam.esm.repository;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.Order;
import com.epam.esm.mapper.CostAndDateOfBuyDtoRowMapper;
import com.epam.esm.mapper.OrderDtoRowMapper;
import com.epam.esm.mapper.UserDtoRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<UserDto> findAll() {
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, new UserDtoRowMapper());
    }

    @Override
    public List<UserDto> findById(Integer id) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        return jdbcTemplate.query(sql, new UserDtoRowMapper(), id);
    }

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO `order`(user_id, gift_id, cost, date_of_buy, gift_name) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, order.getUserId(), order.getGiftId(), order.getCost(), Timestamp.from(Instant.now()), order.getGiftName());
    }

    @Override
    public List<OrderDto> findOrdersByUserId(Integer id) {
        String sql = "SELECT o.* FROM `order` o WHERE user_id = ? ORDER BY order_id";
        return jdbcTemplate.query(sql, new OrderDtoRowMapper(), id);
    }

    @Override
    public List<CostAndDateOfBuyDto> findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
        String sql = "SELECT o.cost, o.date_of_buy FROM `order` o WHERE user_id = ? AND order_id = ?";
        return jdbcTemplate.query(sql, new CostAndDateOfBuyDtoRowMapper(), userId, orderId);
    }
}
