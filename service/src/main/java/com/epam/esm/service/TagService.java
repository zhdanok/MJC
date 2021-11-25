package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.TagDao;
import com.epam.esm.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.epam.esm.util.ExceptionUtils.*;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private final TagDao tagDao;

    private final Converter<Tag, TagDto> converter;

    /**
     * Send request for saving Tag
     *
     * @param tagDto - Dto of Entity which need to save
     * @return Integer - id of new Tag (or of existed Tag if it existed)
     */
    @Transactional
    public Integer save(TagDto tagDto) {
        Tag tag = tagRepository.findTagByName(tagDto.getName()).orElseGet(() -> tagRepository.save(converter.convertToEntity(tagDto)));
        return tag.getId();
    }

    /**
     * Send request for getting Tags with required page and limit
     *
     * @param pageable - interface which contains the information about the requested page such as the size and the number of the page
     * @return List of TagDtos with requirement parameters
     */
    public Page<TagDto> getTags(Pageable pageable) {
        checkForBadRequestException(pageable.getPageNumber() < 0, String.format("Invalid page --> %d", pageable.getPageNumber()), ERR_CODE_TAG);
        checkForBadRequestException(pageable.getPageSize() <= 0, String.format("Invalid limit --> %d", pageable.getPageSize()), ERR_CODE_TAG);
        Page<Tag> tags = tagRepository.findAll(pageable);
        checkForNotFoundException(tags.getContent().isEmpty(), "Tags not found", ERR_CODE_TAG);
        return tags.map(converter::convertToDto);
    }

    /**
     * Send request for getting Tag by Tag's Id
     *
     * @param id - id of Tag which need to get
     * @return TagDto
     */
    public TagDto getTagById(Integer id) {
        checkForBadRequestException((id <= 0), String.format("Invalid id --> %d", id), ERR_CODE_TAG);
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Tag Not Found: id --> %d", id), ERR_CODE_TAG));
        return converter.convertToDto(tag);
    }

    /**
     * Send request for deleting Tag by Tag's Id
     *
     * @param id - id of Tag which need to delete
     */
    @Transactional
    public void deleteById(Integer id) {
        checkForBadRequestException((id <= 0), String.format("Invalid id --> %d", id), ERR_CODE_TAG);
        checkForBadRequestException((!tagRepository.findById(id).isPresent()), String.format("No Tag Found to delete: id --> %d", id), ERR_CODE_TAG);
        tagRepository.deleteById(id);
    }

    /**
     * Send request for getting the most widely used tag of a user with the highest cost
     * of all orders
     *
     * @return TagDto
     */
    public TagDto getMostPopularTagOfUserWithHighestCostOfOrder() {
        Tag tag = tagDao.findMostPopularTagOfUserWithHighestCostOfOrder();
        checkForNotFoundException(tag == null, String.format("There are no Tags"), ERR_CODE_TAG);
        return converter.convertToDto(tag);
    }
}
