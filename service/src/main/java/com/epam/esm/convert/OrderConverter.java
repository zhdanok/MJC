package com.epam.esm.convert;

import com.epam.esm.dto.UsersOrderDto;
import com.epam.esm.entity.UsersOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConverter implements Converter<UsersOrder, UsersOrderDto> {

	private final ModelMapper modelMapper;

	@Override
	public UsersOrder convertToEntity(UsersOrderDto usersOrderDto) {
		return modelMapper.map(usersOrderDto, UsersOrder.class);
	}

	@Override
	public UsersOrderDto convertToDto(UsersOrder usersOrder) {
		return modelMapper.map(usersOrder, UsersOrderDto.class);
	}

}
