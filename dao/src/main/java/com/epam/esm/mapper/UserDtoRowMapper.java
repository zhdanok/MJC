package com.epam.esm.mapper;

import com.epam.esm.dto.UserDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDtoRowMapper implements RowMapper<UserDto> {

    @Override
    public UserDto mapRow(ResultSet rs, int i) throws SQLException {
        UserDto dto = UserDto.builder()
                .id(rs.getInt("user_id"))
                .name(rs.getString("user_name"))
                .build();
        return dto;
    }
}
