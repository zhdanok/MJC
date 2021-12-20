package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.CostAndDateOfBuyDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.dto.UsersOrderDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.UserProfile;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.UsersOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.epam.esm.util.ExceptionUtils.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UsersOrderRepository usersOrderRepository;

    private final GiftCertificateRepository giftCertificateRepository;

    private final Converter<UserProfile, UserDto> converter;

    private final Converter<UsersOrder, UsersOrderDto> converterForOrder;

    /**
     * Send request for getting all UserDtos with required page and limit
     *
     * @param pageable - interface which contains the information about the requested page such as the size and the number of the page
     * @return List of UserDto with requirement parameters
     */
    public Page<UserDto> getUsers(Pageable pageable) {
        Page<UserProfile> list = userRepository.findAll(pageable);
        checkForNotFoundException(list.getContent().isEmpty(), "Users not found", ERR_CODE_USER);
        return list.map(converter::convertToDto);
    }

    /**
     * Send request for getting UserDto by id
     *
     * @param id - Integer
     * @return UserDto
     */
    public UserDto getUserById(Integer id) {
        checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id), ERR_CODE_USER);
        UserProfile userProfile = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id '%d' not found", id), ERR_CODE_USER));
        return converter.convertToDto(userProfile);
    }


    /**
     * Send request for saving Order for User by Id
     *
     * @param userId - User's id, who want to create order
     * @param dto    - UsersOrderDto which need to save
     */
    @Transactional
    public void save(Integer userId, UsersOrderDto dto) {
        GiftCertificate gc = giftCertificateRepository.findById(dto.getGiftId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Gift Certificate with id '%d' not found", dto.getGiftId()),
                ERR_CODE_GIFT));
        UsersOrder usersOrder = UsersOrder.builder()
                .userId(userId)
                .giftId(gc.getId())
                .giftName(gc.getName())
                .cost(gc.getPrice())
                .dateOfBuy(Instant.now())
                .build();
        usersOrderRepository.save(usersOrder);
    }

    /**
     * Send request for saving User
     *
     * @param dto - Dto of Entity which need to save
     * @return Integer - id of new User (or of existed User if it existed)
     */
    @Transactional
    public Integer saveUser(UserDto dto) {
        UserProfile userProfile = userRepository.findUserProfileByLogin(dto.getLogin()).orElseGet(() -> userRepository.save(converter.convertToEntity(dto)));
        return userProfile.getUserId();
    }

    /**
     * Send request for getting User's orders
     *
     * @param id - Integer - User's id
     * @return List of OrderDto
     */
    public Page<UsersOrderDto> getOrdersByUserId(Integer id, Pageable pageable) {
        checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id), ERR_CODE_ORDER);
        userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id '%d' not found", id), ERR_CODE_USER));
        Page<UsersOrder> pages = usersOrderRepository.findAllByUserId(id, pageable);
        checkForNotFoundException(pages.getContent().isEmpty(), String.format("Orders for User with id '%d' not found", id),
                ERR_CODE_ORDER);
        return pages.map(converterForOrder::convertToDto);
    }

    /**
     * Send request for getting Cost and Date of buy for User's order by Order id
     *
     * @param userId  - Integer - User's id
     * @param orderId - Integer - Order's id
     * @return CostAndDateOfBuyDto
     */
    public CostAndDateOfBuyDto getCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
        checkForBadRequestException(userId <= 0, String.format("Invalid User's id --> %d", userId), ERR_CODE_USER);
        checkForBadRequestException(orderId <= 0, String.format("Invalid Order's id --> %d", orderId), ERR_CODE_ORDER);
        UsersOrder usersOrder = usersOrderRepository.findUsersOrderByUserIdAndOrderId(userId, orderId).orElseThrow(() -> new ResourceNotFoundException(String.format("Order with User's id '%d' and Order's id '%d' not found", userId, orderId),
                ERR_CODE_ORDER));
        return CostAndDateOfBuyDto.builder().cost(usersOrder.getCost())
                .dateOfBuy(usersOrder.getDateOfBuy()).build();
    }
}
