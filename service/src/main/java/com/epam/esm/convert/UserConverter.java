package com.epam.esm.convert;

import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter implements Converter<User, UserDto> {

	private final ModelMapper modelMapper;

	@Override
	public User convertToEntity(UserDto userDto) {
		return modelMapper.map(userDto, User.class);
	}

	@Override
	public UserDto convertToDto(User user) {
		return modelMapper.map(user, UserDto.class);
	}

}
