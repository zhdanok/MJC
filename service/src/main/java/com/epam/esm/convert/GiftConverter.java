package com.epam.esm.convert;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GiftConverter implements Converter<GiftCertificate, GiftAndTagDto> {

    private final ModelMapper modelMapper;

    private final Converter<Tag, TagDto> tagDtoConverter;

    @Override
    public GiftCertificate convertToEntity(GiftAndTagDto dto) {
        return modelMapper.map(dto, GiftCertificate.class);
    }

    @Override
    public GiftAndTagDto convertToDto(GiftCertificate giftCertificate) {
        List<TagDto> tags = giftCertificate.getTags().stream().map(tagDtoConverter::convertToDto)
                .collect(Collectors.toList());
        GiftAndTagDto dto = modelMapper.map(giftCertificate, GiftAndTagDto.class);
        dto.setTags(tags);
        return dto;
    }

}
