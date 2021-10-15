package com.epam.esm.controller;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserDto>> getGiftCertificates() {
        List<UserDto> list = userService.getUsers();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<List<UserDto>> getGiftCertificateById(@PathVariable Integer id ) {
        List<UserDto> list  = userService.getUserById(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
