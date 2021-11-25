package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.SearchTags;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.GiftCertificateDao;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.SearchTagRepository;
import com.epam.esm.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.esm.util.ExceptionUtils.*;

@Service
@RequiredArgsConstructor
public class GiftCertificateService {

    private final TagRepository tagRepository;

    private final SearchTagRepository searchTagRepository;

    private final GiftCertificateDao giftCertificateDao;

    private final GiftCertificateRepository giftCertificateRepository;

    private final Converter<GiftCertificate, GiftAndTagDto> converterForGift;

    private final Converter<Tag, TagDto> converterForTag;

    /**
     * Send request for getting Certificate by id
     *
     * @param id - Integer
     * @return Dto which contains Certificate with Tags
     */
    public GiftAndTagDto getCertificateById(Integer id) {
        GiftCertificate gc = giftCertificateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Gift Certificate with id '%d' not found", id),
                ERR_CODE_GIFT));
        return converterForGift.convertToDto(gc);
    }

    /**
     * Send request for getting Certificates with Tags. There is possible to choose with
     * any params or without it (return all Certificates with their Tags)
     *
     * @param tagNames - Array of Tag's name(optional, can be one or several)
     * @param page     - Number of Page (optional)
     * @param limit    - Limit of results at Page (optional)
     * @param substr   - String - substring that can be contained into name or description
     *                 (optional)
     * @param sort     - String - field of sorting (optional)
     * @return List of Dto which contains Certificates and Tags
     */
    public List<GiftAndTagDto> getCertificatesByAnyParams(String[] tagNames, String substr, String[] sort,
                                                          Integer page, Integer limit) {
        Long size = ifExistThenSaveSearchTagsAndReturnSize(tagNames);
        Integer skip = (page - 1) * limit;
        List<GiftCertificate> list = giftCertificateDao.findByAnyParams(size, substr, skip, limit, sort);
        checkForNotFoundException(list.isEmpty(), "Gift Certificates with this parameters not found", ERR_CODE_GIFT);
        return list.stream().map(converterForGift::convertToDto).collect(Collectors.toList());
    }

    /**
     * Save all SearchTags in database if their exist, return count of Tags
     *
     * @param tagNames - Array of Tag's name(optional, can be one or several)
     * @return size - count of Tags
     */
    public Long ifExistThenSaveSearchTagsAndReturnSize(String[] tagNames) {
        Long size = null;

        if (tagNames != null) {
            checkForBadRequestException(isInvalidTags(tagNames), "Invalid tag in request parameters", ERR_CODE_GIFT);
            size = Long.valueOf(tagNames.length);
            searchTagRepository.clear();
            Arrays.stream(tagNames).forEach(s -> searchTagRepository.save(SearchTags.builder().name(s).build()));
        }
        return size;
    }

    private boolean isInvalidTags(String[] tagNames) {
        return Arrays.stream(tagNames).map(name -> tagRepository.findTagByName(name)).anyMatch(tag -> tag == null);
    }

    /**
     * Send request for saving GiftAndTagDto
     *
     * @param dto - instance of GiftAndTagDto
     * @return Integer - id of the entity that was saved
     */
    @Transactional
    public Integer save(GiftAndTagDto dto) {
        GiftCertificate giftCertificate = prepareEntityForSave(dto);
        GiftCertificate gc = giftCertificateRepository.findGiftCertificateByName(giftCertificate.getName()).orElse(null);
        if (gc == null) {
            giftCertificateRepository.save(giftCertificate);
            return giftCertificateRepository.findGiftCertificateByName(giftCertificate.getName()).get().getId();
        }
        return gc.getId();

    }

    private GiftCertificate prepareEntityForSave(GiftAndTagDto dto) {
        List<TagDto> tags = dto.getTags();
        for (TagDto tagDto : tags) {
            Integer id = tagRepository.findTagIdByName(tagDto.getName()).orElse(null);
            if (id == null) {
                Tag newTag = tagRepository.save(converterForTag.convertToEntity(tagDto));
                tagDto.setId(newTag.getId());
            } else {
                tagDto.setId(id);
            }
        }
        dto.setTags(tags);
        dto.setCreateDate(Instant.now());
        dto.setLastUpdateDate(Instant.now());
        GiftCertificate giftCertificate = converterForGift.convertToEntity(dto);
        return giftCertificate;
    }

    /**
     * Send request for deleting GiftAndTagDto
     *
     * @param id - Integer id
     */
    @Transactional
    public void deleteById(Integer id) {
        checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id), ERR_CODE_GIFT);
        checkForBadRequestException((!giftCertificateRepository.findById(id).isPresent()), String.format("No Certificates Found to delete: id --> %d", id), ERR_CODE_TAG);
        giftCertificateRepository.deleteById(id);
    }

    /**
     * Send request for updating only fields in GiftAndTagDto
     *
     * @param id      - Integer id
     * @param updates - Map<String, Object>, String - name of field, Object - value of
     *                field
     */
    @Transactional
    public void update(Map<String, Object> updates, Integer id) {
        checkForBadRequestException(id <= 0, "Invalid id", ERR_CODE_GIFT);
        int size = giftCertificateDao.update(updates, id, Instant.now());
        checkForNotFoundException(size == 0, String.format("No Certificates Found to update: id --> %d", id),
                ERR_CODE_GIFT);
    }

    /**
     * Return count of Results which match the search parameters
     *
     * @param tagNames - Array of Tag's name(optional, can be one or several)
     * @return size - count of Results which match the search parameters
     */
    public Long getSize(String[] tagNames, String substr) {
        Long size = (tagNames != null) ? Long.valueOf(tagNames.length) : null;
        return giftCertificateDao.findSize(size, substr);
    }

}
