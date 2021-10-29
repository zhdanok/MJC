package com.epam.esm.controller;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.dto.UsersOrderDto;
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

	private static final Integer NUMBER_OF_FIRST_PAGE = 1;

	private final UserService userService;

	@GetMapping(value = "/users", produces = {"application/hal+json"})
	public CollectionModel<UserDto> getUsers(@RequestParam(value = "page", defaultValue = "1") Integer page,
											 @RequestParam(value = "limit", defaultValue = "2") Integer limit) {
		List<UserDto> list = userService.getUsers(page, limit);
		for (UserDto dto : list) {
			Integer id = dto.getId();
			dto.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
		}
		return getCollectionModelWithPagination(page, limit, list);
	}

	private CollectionModel<UserDto> getCollectionModelWithPagination(Integer page, Integer limit, List<UserDto> list) {
		Long sizeOfList = userService.getSize();
		Integer lastPage = Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
		Integer firstPage = NUMBER_OF_FIRST_PAGE;
		Integer nextPage = (page.equals(lastPage)) ? lastPage : page + 1;
		Integer prevPage = (page.equals(firstPage)) ? firstPage : page - 1;
		Link self = linkTo(methodOn(UserController.class).getUsers(page, limit)).withSelfRel();
		Link next = linkTo(methodOn(UserController.class).getUsers(nextPage, limit)).withRel("next");
		Link prev = linkTo(methodOn(UserController.class).getUsers(prevPage, limit)).withRel("prev");
		Link first = linkTo(methodOn(UserController.class).getUsers(firstPage, limit)).withRel("first");
		Link last = linkTo(methodOn(UserController.class).getUsers(lastPage, limit)).withRel("last");
		return CollectionModel.of(list, first, prev, self, next, last);
	}

	@GetMapping(value = "/users/{userId}", produces = {"application/hal+json"})
	public ResponseEntity<UserDto> getUserById(@PathVariable Integer userId) {
		UserDto dto = userService.getUserById(userId);
		dto.add(linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping(value = "/users/{userId}/orders", produces = {"application/hal+json"})
	public CollectionModel<UsersOrderDto> getOrdersByUserId(@PathVariable Integer userId,
															@RequestParam(value = "page", defaultValue = "1") Integer page,
															@RequestParam(value = "limit", defaultValue = "2") Integer limit) {
		List<UsersOrderDto> list = userService.getOrdersByUserId(userId, page, limit);
		for (UsersOrderDto dto : list) {
			dto.add(linkTo(methodOn(UserController.class).getCostAndDateOfBuyForUserByOrderId(userId, dto.getOrderId()))
					.withSelfRel());
		}
		return getCollectionModelWithPagination(userId, page, limit, list);
	}

	private CollectionModel<UsersOrderDto> getCollectionModelWithPagination(Integer userId, Integer page, Integer limit,
																			List<UsersOrderDto> list) {
		Long sizeOfList = userService.getUsersOrdersSize(userId);
		Integer lastPage = Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
		Integer firstPage = NUMBER_OF_FIRST_PAGE;
		Integer nextPage = (page.equals(lastPage)) ? lastPage : page + 1;
		Integer prevPage = (page.equals(firstPage)) ? firstPage : page - 1;
		Link self = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, page, limit)).withSelfRel();
		Link next = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, nextPage, limit)).withRel("next");
		Link prev = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, prevPage, limit)).withRel("prev");
		Link first = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, firstPage, limit))
				.withRel("first");
		Link last = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, lastPage, limit)).withRel("last");
		return CollectionModel.of(list, first, prev, self, next, last);
	}

	@PostMapping(value = "/users/{userId}/orders")
	public ResponseEntity<?> postOrder(@PathVariable Integer userId, @RequestBody UsersOrderDto dto) {
		userService.save(userId, dto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(value = "/users/{userId}/orders/{orderId}", produces = {"application/hal+json"})
	public ResponseEntity<CostAndDateOfBuyDto> getCostAndDateOfBuyForUserByOrderId(@PathVariable Integer userId,
																				   @PathVariable Integer orderId) {
		CostAndDateOfBuyDto dto = userService.getCostAndDateOfBuyForUserByOrderId(userId, orderId);
		dto.add(linkTo(methodOn(UserController.class).getCostAndDateOfBuyForUserByOrderId(userId, orderId))
				.withSelfRel());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

}
