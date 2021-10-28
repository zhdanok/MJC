package com.epam.esm.convert;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.entity.UsersOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConverter implements Converter<UsersOrder, OrderDto> {

    private final ModelMapper modelMapper;

    @Override
    public UsersOrder convertToEntity(OrderDto orderDto) {
        return modelMapper.map(orderDto, UsersOrder.class);
    }

    @Override
    public OrderDto convertToDto(UsersOrder usersOrder) {
        return modelMapper.map(usersOrder, OrderDto.class);
    }
}
