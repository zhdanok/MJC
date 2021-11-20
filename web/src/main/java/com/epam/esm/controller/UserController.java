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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param page  - number of page with required limit (default value = 1)
     * @param limit - count of Users which need to view at page (default value = 2)
     * @return CollectionModel with UserDto with pagination and links (HATEOAS)
     */
    @GetMapping(value = "/users", produces = {"application/hal+json"})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CollectionModel<UserDto> getUsers(@AuthenticationPrincipal OAuth2User oAuth2User,
                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<UserDto> list = userService.getUsers(page, limit);
        for (UserDto dto : list) {
            Integer id = dto.getId();
            dto.add(linkTo(methodOn(UserController.class).getUserById(oAuth2User, id)).withSelfRel());
        }
        return getCollectionModelWithPagination(oAuth2User, page, limit, list);
    }

    /**
     * Send request for getting UserDto by User's id
     *
     * @param userId - id of User which need to get
     * @return ResponseEntity with UserDto and link (HATEOAS)
     */
    @GetMapping(value = "/users/{userId}", produces = {"application/hal+json"})
    @PreAuthorize("hasAnyAuthority({'SCOPE_ADMIN', 'SCOPE_USER'})")
    public ResponseEntity<UserDto> getUserById(@AuthenticationPrincipal OAuth2User oAuth2User, @PathVariable Integer userId) {
        UserDto dto = userService.getUserById(oAuth2User, userId);
        dto.add(linkTo(methodOn(UserController.class).getUserById(oAuth2User, userId)).withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Send request for getting UsersOrders by User's id with required page and limit
     *
     * @param userId - User's id
     * @param page   - number of page with required limit (default value = 1)
     * @param limit  - count of UsersOrder which need to view at page (default value = 2)
     * @return CollectionModel with UsersOrderDto with pagination and links (HATEOAS)
     */
    @GetMapping(value = "/users/{userId}/orders", produces = {"application/hal+json"})
    @PreAuthorize("hasAnyAuthority({'SCOPE_ADMIN', 'SCOPE_USER'})")
    public CollectionModel<UsersOrderDto> getOrdersByUserId(@AuthenticationPrincipal OAuth2User oAuth2User,
                                                            @PathVariable Integer userId,
                                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "limit", defaultValue = "2") Integer limit) {
        List<UsersOrderDto> list = userService.getOrdersByUserId(oAuth2User, userId, page, limit);
        for (UsersOrderDto dto : list) {
            dto.add(linkTo(methodOn(UserController.class).getCostAndDateOfBuyForUserByOrderId(oAuth2User, userId, dto.getOrderId()))
                    .withSelfRel());
        }
        return getCollectionModelWithPagination(oAuth2User, userId, page, limit, list);
    }

    /**
     * Send request for saving User
     *
     * @param dto - Dto of Entity which need to save
     * @return ResponseEntity with link of new User (or of existed User if it existed)
     */
    @PostMapping(value = "/users", consumes = {"application/json"}, produces = {"application/hal+json"})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Link> postUser(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestBody UserDto dto) {
        Integer id = userService.saveUser(dto);
        Link link = linkTo(methodOn(UserController.class).getUserById(oAuth2User, id)).withSelfRel();
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }

    /**
     * Send request for saving UsersOrder
     *
     * @param dto - Dto of Entity which need to save
     */
    @PostMapping(value = "/users/{userId}/orders", consumes = {"application/json"},
            produces = {"application/hal+json"})
    @PreAuthorize("hasAnyAuthority({'SCOPE_ADMIN', 'SCOPE_USER'})")
    public ResponseEntity<?> postOrder(@AuthenticationPrincipal OAuth2User oAuth2User,
                                       @PathVariable Integer userId,
                                       @RequestBody UsersOrderDto dto) {
        userService.save(oAuth2User, userId, dto);
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
    public ResponseEntity<CostAndDateOfBuyDto> getCostAndDateOfBuyForUserByOrderId(@AuthenticationPrincipal OAuth2User oAuth2User,
                                                                                   @PathVariable Integer userId,
                                                                                   @PathVariable Integer orderId) {
        CostAndDateOfBuyDto dto = userService.getCostAndDateOfBuyForUserByOrderId(oAuth2User, userId, orderId);
        dto.add(linkTo(methodOn(UserController.class).getCostAndDateOfBuyForUserByOrderId(oAuth2User, userId, orderId))
                .withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private CollectionModel<UserDto> getCollectionModelWithPagination(OAuth2User oAuth2User, Integer page, Integer limit, List<UserDto> list) {
        Long sizeOfList = userService.getSize();
        Integer lastPage = Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
        Integer firstPage = NUMBER_OF_FIRST_PAGE;
        Integer nextPage = (page.equals(lastPage)) ? lastPage : page + 1;
        Integer prevPage = (page.equals(firstPage)) ? firstPage : page - 1;
        Link self = linkTo(methodOn(UserController.class).getUsers(oAuth2User, page, limit)).withSelfRel();
        Link next = linkTo(methodOn(UserController.class).getUsers(oAuth2User, nextPage, limit)).withRel("next");
        Link prev = linkTo(methodOn(UserController.class).getUsers(oAuth2User, prevPage, limit)).withRel("prev");
        Link first = linkTo(methodOn(UserController.class).getUsers(oAuth2User, firstPage, limit)).withRel("first");
        Link last = linkTo(methodOn(UserController.class).getUsers(oAuth2User, lastPage, limit)).withRel("last");
        return CollectionModel.of(list, first, prev, self, next, last);
    }

    private CollectionModel<UsersOrderDto> getCollectionModelWithPagination(OAuth2User oAuth2User, Integer userId, Integer page, Integer limit,
                                                                            List<UsersOrderDto> list) {
        Long sizeOfList = userService.getUsersOrdersSize(userId);
        Integer lastPage = Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
        Integer firstPage = NUMBER_OF_FIRST_PAGE;
        Integer nextPage = (page.equals(lastPage)) ? lastPage : page + 1;
        Integer prevPage = (page.equals(firstPage)) ? firstPage : page - 1;
        Link self = linkTo(methodOn(UserController.class).getOrdersByUserId(oAuth2User, userId, page, limit)).withSelfRel();
        Link next = linkTo(methodOn(UserController.class).getOrdersByUserId(oAuth2User, userId, nextPage, limit)).withRel("next");
        Link prev = linkTo(methodOn(UserController.class).getOrdersByUserId(oAuth2User, userId, prevPage, limit)).withRel("prev");
        Link first = linkTo(methodOn(UserController.class).getOrdersByUserId(oAuth2User, userId, firstPage, limit))
                .withRel("first");
        Link last = linkTo(methodOn(UserController.class).getOrdersByUserId(oAuth2User, userId, lastPage, limit)).withRel("last");
        return CollectionModel.of(list, first, prev, self, next, last);
    }

}
