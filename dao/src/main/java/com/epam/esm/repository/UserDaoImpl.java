package com.epam.esm.repository;

import com.epam.esm.dto.UserDto;
import com.epam.esm.mapper.TagDtoRowMapper;
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
    public List<UserDto> findAll() {
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, new UserDtoRowMapper());
    }

    @Override
    public List<UserDto> findById(Integer id) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        return jdbcTemplate.query(sql, new UserDtoRowMapper(), id);
    }
}
