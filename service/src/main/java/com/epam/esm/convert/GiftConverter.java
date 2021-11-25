package com.epam.esm.convert;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GiftConverter implements Converter<GiftCertificate, GiftAndTagDto> {

	private final ModelMapper modelMapper;

	private final Converter<Tag, TagDto> tagDtoConverter;

	@Override
	public GiftCertificate convertToEntity(GiftAndTagDto dto) {
		GiftCertificate gc = modelMapper.map(dto, GiftCertificate.class);
		if (!dto.getTags().isEmpty()) {
			Set<Tag> tags = dto.getTags().stream()
					.map(tagDtoConverter::convertToEntity)
					.collect(Collectors.toSet());
			gc.setTags(tags);
		}
		return gc;
	}

	@Override
	public GiftAndTagDto convertToDto(GiftCertificate giftCertificate) {
		GiftAndTagDto dto = modelMapper.map(giftCertificate, GiftAndTagDto.class);
		if (giftCertificate.getTags() != null) {
			List<TagDto> tags = giftCertificate.getTags().stream().map(tagDtoConverter::convertToDto)
					.collect(Collectors.toList());
			dto.setTags(tags);
		}

		return dto;
	}

}
