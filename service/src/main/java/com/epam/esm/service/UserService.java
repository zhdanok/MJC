package com.epam.esm.service;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.epam.esm.util.checkUtil.checkForNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    /**
     * Send request for getting all Users
     *
     * @return List of UserDto
     */
    public List<UserDto> getUsers() {
        List<UserDto> list = userDao.findAll();
        checkForNotFoundException(list.isEmpty(), "Users not found");
        return list;
    }

    /**
     * Send request for getting User by id
     *
     * @param id - Integer
     * @return UserDto
     */
    public List<UserDto> getUserById(Integer id) {
        List<UserDto> list = userDao.findById(id);
        checkForNotFoundException(list.isEmpty(), String.format("User with id '%d' not found", id));
        return list;
    }
}
