package com.epam.esm.convert;

import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter implements Converter<UserProfile, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserProfile convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserProfile.class);
    }

    @Override
    public UserDto convertToDto(UserProfile userProfile) {
        return modelMapper.map(userProfile, UserDto.class);
    }

}
