package com.epam.esm.repository;

import com.epam.esm.dto.UserDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    List<UserDto> findAll();

    List<UserDto> findById(Integer id);
}
