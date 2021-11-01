package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.util.ExceptionUtils.*;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagDao tagDao;

	private final Converter<Tag, TagDto> converter;

	/**
	 * Send request for saving Tag
	 * @param tagDto - Dto of Entity which need to save
	 * @return Integer - id of new Tag (or of existed Tag if it existed)
	 */
	@Transactional
	public Integer save(TagDto tagDto) {
		Tag tag = converter.convertToEntity(tagDto);
		Integer id = tagDao.findTagIdByTagName(tag.getName());
		if (id == null) {
			tagDao.save(tag);
			return tagDao.findTagIdByTagName(tag.getName());
		}
		return id;
	}

	/**
	 * Send request for getting Tags with required page and limit
	 *
	 * @param page  - number of page with required limit
	 * @param limit - count of Tags which need to view at page
	 * @return List of TagDtos with requirement parameters
	 */
	public List<TagDto> getTags(Integer page, Integer limit) {
		checkForBadRequestException(page <= 0 || page > getLastPage(limit), String.format("Invalid page --> %d", page),
				ERR_CODE_TAG);
		checkForBadRequestException(limit <= 0, String.format("Invalid limit --> %d", limit), ERR_CODE_TAG);
		Integer skip = (page - 1) * limit;
		List<Tag> list = tagDao.findAll(skip, limit);
		checkForNotFoundException(list.isEmpty(), "Tags not found", ERR_CODE_TAG);
		return list.stream().map(converter::convertToDto).collect(Collectors.toList());
	}

	/**
	 * Send request for getting number of last Page with required limit
	 * @param limit - count of Tags which need to view at page
	 * @return Long - number of last page
	 */
	public Long getLastPage(Integer limit) {
		Long sizeOfList = getSize();
		return (sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit;
	}

	/**
	 * Send request for getting Tag by Tag's Id
	 * @param id - id of Tag which need to get
	 * @return TagDto
	 */
	public TagDto getTagById(Integer id) {
		checkForBadRequestException((id <= 0), String.format("Invalid id --> %d", id), ERR_CODE_TAG);
		Tag tag = tagDao.findById(id);
		checkForNotFoundException(tag == null, String.format("Tag Not Found: id --> %d", id), ERR_CODE_TAG);
		return converter.convertToDto(tag);
	}

	/**
	 * Send request for deleting Tag by Tag's Id
	 * @param id - id of Tag which need to delete
	 */
	@Transactional
	public void deleteById(Integer id) {
		checkForBadRequestException((id <= 0), String.format("Invalid id --> %d", id), ERR_CODE_TAG);
		int size = tagDao.deleteById(id);
		checkForNotFoundException(size == 0, String.format("No Tag Found to delete: id --> %d", id), ERR_CODE_TAG);
	}

	/**
	 * Send request for getting the most widely used tag of a user with the highest cost
	 * of all orders
	 * @return TagDto
	 */
	public TagDto getMostPopularTagOfUserWithHighestCostOfOrder() {
		Tag tag = tagDao.findMostPopularTagOfUserWithHighestCostOfOrder();
		checkForNotFoundException(tag == null, String.format("There are no Tags"), ERR_CODE_TAG);
		return converter.convertToDto(tag);
	}

	/**
	 * Send request for getting count of all Tags
	 * @return Long
	 */
	public Long getSize() {
		return tagDao.findSize();
	}

}
