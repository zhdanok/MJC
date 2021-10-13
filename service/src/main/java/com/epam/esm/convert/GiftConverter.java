package com.epam.esm.convert;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GiftConverter implements Converter<GiftCertificate, GiftAndTagDto> {

    private final ModelMapper modelMapper;

    @Override
    public GiftCertificate convertToEntity(GiftAndTagDto dto) {
        return modelMapper.map(dto, GiftCertificate.class);

    }
}





