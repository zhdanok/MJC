package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.util.checkUtil.checkForBadRequestException;
import static com.epam.esm.util.checkUtil.checkForNotFoundException;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagDao tagDao;

	private final Converter<Tag, TagDto> converter;

	@Transactional
	public void save(TagDto tagDto) {
		Tag tag = converter.convertToEntity(tagDto);
		Integer id = tagDao.findTagIdByTagName(tag.getName());
		if (id == null) {
			tagDao.save(tag);
		}
	}

	public List<TagDto> getTags(Integer page, Integer limit) {
		checkForBadRequestException(page <= 0 || page > getLastPage(limit), String.format("Invalid page --> %d", page));
		checkForBadRequestException(limit <= 0, String.format("Invalid limit --> %d", page));
		Integer skip = (page - 1) * limit;
		List<Tag> list = tagDao.findAll(skip, limit);
		checkForNotFoundException(list.isEmpty(), "Tags not found");
		return list.stream().map(converter::convertToDto).collect(Collectors.toList());
	}

	public Long getLastPage(Integer limit) {
		Long sizeOfList = getSize();
		return (sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit;
	}

	public TagDto getTagById(Integer id) {
		if (id <= 0) {
			throw new BadRequestException(String.format("Invalid id --> %d", id));
		}
		Tag tag = tagDao.findById(id);
		if (tag == null) {
			throw new ResourceNotFoundException(String.format("Tag Not Found: id --> %d", id));
		}
		return converter.convertToDto(tag);
	}

	@Transactional
	public void deleteById(Integer id) {
		if (id <= 0) {
			throw new BadRequestException(String.format("Invalid id --> %d", id));
		}
		int size = tagDao.deleteById(id);
		if (size == 0) {
			throw new ResourceNotFoundException(String.format("No Tag Found to delete: id --> %d", id));
		}
	}

	/**
	 * Send request for getting the most widely used tag of a user with the highest cost
	 * of all orders
	 *
	 * @return TagDto
	 */
	public TagDto getMostPopularTagOfUserWithHighestCostOfOrder() {
		Tag tag = tagDao.findMostPopularTagOfUserWithHighestCostOfOrder();
		if (tag == null) {
			throw new ResourceNotFoundException(String.format("There ara not Tags"));
		}
		return converter.convertToDto(tag);
	}

	public Long getSize() {
		return tagDao.findSize();
	}

}
