package com.epam.esm.controller;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> list = userService.getUsers();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{userId}")
    public ResponseEntity<List<UserDto>> getUserById(@PathVariable Integer userId) {
        List<UserDto> list = userService.getUserById(userId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{userId}/orders")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderDto> list = userService.getOrdersByUserId(userId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/users/{userId}/orders")
    public ResponseEntity<?> postOrder(@PathVariable Integer userId,
                                       @RequestBody Integer giftId) {
        userService.save(userId, giftId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/users/{userId}/orders/{orderId}")
    public ResponseEntity<List<CostAndDateOfBuyDto>> getCostAndDateOfBuyForUserByOrderId(@PathVariable Integer userId,
                                                                                         @PathVariable Integer orderId) {
        List<CostAndDateOfBuyDto> list = userService.getCostAndDateOfBuyForUserByOrderId(userId, orderId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
