package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.UserDto;
import com.epam.esm.dto.UsersOrderDto;
import com.epam.esm.entity.UserProfile;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.UsersOrderRepository;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.abstraction.MockUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static net.andreinc.mockneat.unit.objects.Reflect.reflect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = ServiceApplication.class)
@TestPropertySource("classpath:test.properties")
class UserProfileServiceTest {

    @Autowired
    UserService service;

    @Autowired
    Converter<UserProfile, UserDto> converter;
    @MockBean
    UserRepository userRepository;
    @MockBean
    UsersOrderRepository usersOrderRepository;
    @Value("${populate.database}")
    private boolean isNeedPopulateBd;

    @Test
    void getUsers() {
        // given
        Page<UserProfile> mockList = getMockPage();
        Page<UserDto> expList = getExpPage();
        Pageable pageable = PageRequest.of(2, 2);

        // when
        when(userRepository.findAll(pageable)).thenReturn(mockList);
        Page<UserDto> actualList = service.getUsers(pageable);

        // then
        assertEquals(expList, actualList);
    }

    @Test
    void getUsers_withNotFound() {
        // given
        Pageable pageable = PageRequest.of(3, 2);
        String expected = "Users not found";

        // when
        when(userRepository.findAll(pageable)).thenReturn(Page.empty());
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> service.getUsers(pageable));

        // then
        assertEquals(expected, ex.getMessage());
    }


	@Test
	void saveUser() {
        // given
        Integer expId = 2;
        UserDto dto = UserDto.builder().name("User2").login("user2").build();
        UserProfile mockUserProfile = UserProfile.builder().userId(expId).userName("User2").login("user2").build();

        // when
        when(userRepository.findUserProfileByLogin(dto.getLogin()).orElseGet(() -> userRepository.save(converter.convertToEntity(dto)))).thenReturn(mockUserProfile);
        Integer actualId = service.saveUser(dto);

        // then
        assertEquals(expId, actualId);
    }



    private Page<UserProfile> getMockPage() {
        List<UserProfile> mockList = new ArrayList<>();
        mockList.add(UserProfile.builder().userId(1).userName("User1").login("user1").build());
        mockList.add(UserProfile.builder().userId(2).userName("User2").login("user2").build());
        mockList.add(UserProfile.builder().userId(3).userName("User3").login("user3").build());
        return new PageImpl<>(mockList);
    }

    private Page<UserDto> getExpPage() {
        List<UserDto> expList = new ArrayList<>();
        expList.add(UserDto.builder().id(1).name("User1").login("user1").build());
        expList.add(UserDto.builder().id(2).name("User2").login("user2").build());
        expList.add(UserDto.builder().id(3).name("User3").login("user3").build());
        return new PageImpl<>(expList);
    }

    private Page<UsersOrder> getMockOrderPage() {
        List<UsersOrder> mockList = new ArrayList<>();
        mockList.add(UsersOrder.builder().orderId(1).userId(1).giftId(1).build());
        mockList.add(UsersOrder.builder().orderId(2).userId(1).giftId(2).build());
        mockList.add(UsersOrder.builder().orderId(3).userId(1).giftId(3).build());

        return new PageImpl<>(mockList);
    }

    private Page<UsersOrderDto> getExpOrderPage() {
        List<UsersOrderDto> expList = new ArrayList<>();
        expList.add(UsersOrderDto.builder().orderId(1).userId(1).giftId(1).build());
        expList.add(UsersOrderDto.builder().orderId(2).userId(1).giftId(2).build());
        expList.add(UsersOrderDto.builder().orderId(3).userId(1).giftId(3).build());
        return new PageImpl<>(expList);
    }


    /**
     * This test can generate 1300 new Users and save it to database. If You need to
     * generate it, please change populate.database to true in test.properties and then
     * run the test
     */
    @Test
    void loadDataToTableUser() {
        if (isNeedPopulateBd) {
            MockNeat mockNeat = MockNeat.threadLocal();

            for (int i = 0; i < 1300; i++) {
                MockUnit<UserProfile> rUserGenerator = reflect(UserProfile.class).field("userName", mockNeat.names()).field("login", mockNeat.names().first());
                service.saveUser(converter.convertToDto(rUserGenerator.get()));
            }

        }
    }

    /**
     * This test can generate 5000 new User's Orders and save it to database. If You need
     * to generate it, please change populate.database to true in test.properties and then
     * run the test
     */
    @Test
    void loadDataToTableOrder() {
        if (isNeedPopulateBd) {

            MockNeat mockNeat = MockNeat.threadLocal();

            for (int i = 0; i < 5000; i++) {
                MockUnit<UsersOrderDto> rUserOrderGenerator = reflect(UsersOrderDto.class).field("giftId",
                        mockNeat.ints().range(1, 9021));

                Integer userId = mockNeat.ints().range(1, 1300).get();
                //service.save(userId, rUserOrderGenerator.get());
            }
        }
    }

}
