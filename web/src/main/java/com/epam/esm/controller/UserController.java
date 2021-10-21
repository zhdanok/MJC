package com.epam.esm.controller;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/users", produces = {"application/hal+json"})
    public CollectionModel<UserDto> getUsers() {
        List<UserDto> list = userService.getUsers();
        for (UserDto dto : list) {
            Integer id = dto.getId();
            dto.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        }
        Link link = linkTo(methodOn(UserController.class).getUsers()).withSelfRel();
        return CollectionModel.of(list, link);
    }

    @GetMapping(value = "/users/{userId}", produces = {"application/hal+json"})
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer userId) {
        UserDto dto = userService.getUserById(userId);
        dto.add(linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{userId}/orders", produces = {"application/hal+json"})
    public CollectionModel<OrderDto> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderDto> list = userService.getOrdersByUserId(userId);
        for (OrderDto dto : list) {
            dto.add(linkTo(UserController.class)
                            .slash(userId)
                            .withSelfRel())
                    .add(linkTo(methodOn(UserController.class)
                            .getCostAndDateOfBuyForUserByOrderId(userId, dto.getOrderId()))
                            .withSelfRel());
        }
        Link link = linkTo(methodOn(UserController.class).getOrdersByUserId(userId)).withSelfRel();
        return CollectionModel.of(list, link);
    }

    @PostMapping(value = "/users/{userId}/orders")
    public ResponseEntity<?> postOrder(@PathVariable Integer userId,
                                       @RequestBody Integer giftId) {
        userService.save(userId, giftId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/users/{userId}/orders/{orderId}", produces = {"application/hal+json"})
    public ResponseEntity<CostAndDateOfBuyDto> getCostAndDateOfBuyForUserByOrderId(@PathVariable Integer userId,
                                                                                   @PathVariable Integer orderId) {
        CostAndDateOfBuyDto dto = userService.getCostAndDateOfBuyForUserByOrderId(userId, orderId);
        dto.add(linkTo(UserController.class)
                        .slash(userId)
                        .withSelfRel())
                .add(linkTo(methodOn(UserController.class)
                        .getCostAndDateOfBuyForUserByOrderId(userId, orderId))
                        .withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
