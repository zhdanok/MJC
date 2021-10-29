package com.epam.esm.mapper;

import com.epam.esm.dto.UsersOrderDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDtoRowMapper implements RowMapper<UsersOrderDto> {

    @Override
    public UsersOrderDto mapRow(ResultSet rs, int i) throws SQLException {
        UsersOrderDto dto = UsersOrderDto.builder().orderId(rs.getInt("order_id")).userId(rs.getInt("user_id"))
                .giftId(rs.getInt("gift_id")).cost(rs.getDouble("cost"))
                .dateOfBuy(rs.getTimestamp("date_of_buy").toInstant()).giftName(rs.getString("gift_name")).build();
        return dto;
    }

}
