package com.epam.esm.controller;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.dto.UsersOrderDto;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final Integer NUMBER_OF_FIRST_PAGE = 1;

    private final UserService userService;

    /**
     * Send request for getting UserDtos with required page and limit
     *
     * @param page - number of page with required limit (default value = 0)
     * @param size - count of Users which need to view at page (default value = 10)
     * @return CollectionModel with UserDto with pagination and links (HATEOAS)
     */
    @GetMapping(value = "/users", produces = {"application/hal+json"})
    public CollectionModel<UserDto> getUsers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<UserDto> pages = userService.getUsers(PageRequest.of(page, size, Sort.by("userId").ascending()));
        for (UserDto dto : pages.getContent()) {
            Integer id = dto.getId();
            dto.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        }
        return getCollectionModelWithPagination(pages);
    }

    /**
     * Send request for getting UserDto by User's id
     *
     * @param userId - id of User which need to get
     * @return ResponseEntity with UserDto and link (HATEOAS)
     */
    @GetMapping(value = "/users/{userId}", produces = {"application/hal+json"})
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer userId) {
        UserDto dto = userService.getUserById(userId);
        dto.add(linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Send request for getting UsersOrders by User's id with required page and limit
     *
     * @param userId - User's id
     * @param page   - number of page with required limit (default value = 1)
     * @param size   - count of UsersOrder which need to view at page (default value = 2)
     * @return CollectionModel with UsersOrderDto with pagination and links (HATEOAS)
     */
    @GetMapping(value = "/users/{userId}/orders", produces = {"application/hal+json"})
    public CollectionModel<UsersOrderDto> getOrdersByUserId(
            @PathVariable Integer userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<UsersOrderDto> pages = userService.getOrdersByUserId(userId, PageRequest.of(page, size, Sort.by("orderId").ascending()));
        for (UsersOrderDto dto : pages.getContent()) {
            dto.add(linkTo(methodOn(UserController.class).getCostAndDateOfBuyForUserByOrderId(userId, dto.getOrderId()))
                    .withSelfRel());
        }
        return getCollectionModelWithPagination(userId, pages);
    }

    /**
     * Send request for saving User
     *
     * @param dto - Dto of Entity which need to save
     * @return ResponseEntity with link of new User (or of existed User if it existed)
     */
    @PostMapping(value = "/users", consumes = {"application/json"}, produces = {"application/hal+json"})
    public ResponseEntity<Link> postUser(@RequestBody UserDto dto) {
        Integer id = userService.saveUser(dto);
        Link link = linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel();
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }

    /**
     * Send request for saving UsersOrder
     *
     * @param dto - Dto of Entity which need to save
     */
    @PostMapping(value = "/users/{userId}/orders", consumes = {"application/json"},
            produces = {"application/hal+json"})
    public ResponseEntity<?> postOrder(
            @PathVariable Integer userId,
            @RequestBody UsersOrderDto dto) {
        userService.save(userId, dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Send request for getting Cost and Date of buy of UsersOrder by User's id and
     * Order's id
     *
     * @param userId  - id of User which need to get
     * @param orderId - id of Order which need to get
     * @return ResponseEntity with CostAndDateOfBuyDto and link (HATEOAS)
     */
    @GetMapping(value = "/users/{userId}/orders/{orderId}", produces = {"application/hal+json"})
    public ResponseEntity<CostAndDateOfBuyDto> getCostAndDateOfBuyForUserByOrderId(
            @PathVariable Integer userId,
            @PathVariable Integer orderId) {
        CostAndDateOfBuyDto dto = userService.getCostAndDateOfBuyForUserByOrderId(userId, orderId);
        dto.add(linkTo(methodOn(UserController.class).getCostAndDateOfBuyForUserByOrderId(userId, orderId))
                .withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private CollectionModel<UserDto> getCollectionModelWithPagination(Page<UserDto> pages) {
        Integer lastPage = pages.getTotalPages() - 1;
        Integer firstPage = NUMBER_OF_FIRST_PAGE;
        Integer nextPage = pages.nextOrLastPageable().getPageNumber();
        Integer prevPage = pages.previousOrFirstPageable().getPageNumber();
        Link self = linkTo(methodOn(UserController.class).getUsers(pages.getNumber(), pages.getSize())).withSelfRel();
        Link next = linkTo(methodOn(UserController.class).getUsers(nextPage, pages.getSize())).withRel("next");
        Link prev = linkTo(methodOn(UserController.class).getUsers(prevPage, pages.getSize())).withRel("prev");
        Link first = linkTo(methodOn(UserController.class).getUsers(firstPage, pages.getSize())).withRel("first");
        Link last = linkTo(methodOn(UserController.class).getUsers(lastPage, pages.getSize())).withRel("last");
        return CollectionModel.of(pages, first, prev, self, next, last);
    }

    private CollectionModel<UsersOrderDto> getCollectionModelWithPagination(Integer userId, Page<UsersOrderDto> pages) {
        Integer lastPage = pages.getTotalPages() - 1;
        Integer firstPage = NUMBER_OF_FIRST_PAGE;
        Integer nextPage = pages.nextOrLastPageable().getPageNumber();
        Integer prevPage = pages.previousOrFirstPageable().getPageNumber();
        Link self = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, pages.getNumber(), pages.getSize())).withSelfRel();
        Link next = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, nextPage, pages.getSize())).withRel("next");
        Link prev = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, prevPage, pages.getSize())).withRel("prev");
        Link first = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, firstPage, pages.getSize()))
                .withRel("first");
        Link last = linkTo(methodOn(UserController.class).getOrdersByUserId(userId, lastPage, pages.getSize())).withRel("last");
        return CollectionModel.of(pages, first, prev, self, next, last);
    }

}
