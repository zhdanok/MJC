package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.SearchTag;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.GiftCertificateDao;
import com.epam.esm.repository.SearchTagDao;
import com.epam.esm.repository.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.epam.esm.util.checkUtil.checkForBadRequestException;
import static com.epam.esm.util.checkUtil.checkForNotFoundException;

@Service
@RequiredArgsConstructor
public class GiftCertificateService {

    private final TagDao tagDao;

    private final SearchTagDao searchTagDao;

    private final GiftsTagsService giftsTagsService;

    private final GiftCertificateDao giftCertificateDao;

    private final Converter<GiftCertificate, GiftAndTagDto> converterForGift;

    /**
     * Send request for getting Certificate by id
     *
     * @param id - Integer
     * @return Dto which contains Certificate with Tags
     */
    public List<GiftAndTagDto> getCertificateById(Integer id) {
        List<GiftAndTagDto> list = giftCertificateDao.findById(id);
        checkForNotFoundException(list.isEmpty(), String.format("Gift Certificate with id '%d' not found", id));
        return list;
    }

    /**
     * Send request for getting Certificates with Tags. There is possible to choose with any params
     * or without it (return all Certificates with their Tags)
     *
     * @param tagNames - Array of Tag's name(optional, can be one or several)
     * @param substr   - String - substring that can be contained into name or description (optional)
     * @param sort     - String - style of sorting (optional):
     *                 name-ASC/name-DESC - by Tag's name asc/desc
     *                 date-ASC/date-DESC - by Date of creation asc/desc
     *                 ASC/DESC -           by Tag's name and then by Date of creating asc/desc
     * @return List of Dto which contains Certificates and Tags
     */
    @Transactional
    public List<GiftAndTagDto> getCertificatesByAnyParams(String[] tagNames, String substr, String sort) {
        Integer size = getSizeOrNull(tagNames);
        saveSearchTagsIfExist(tagNames);
        List<GiftAndTagDto> list = giftCertificateDao.findByAnyParams(size, substr);
        searchTagDao.clear();
        checkForNotFoundException(list.isEmpty(), "Gift Certificates with this parameters not found");
        return sortBySortType(sort, list);
    }

    private void saveSearchTagsIfExist(String[] tagNames) {
        if (tagNames != null) {
            Arrays.stream(tagNames).forEach(s -> searchTagDao.save(SearchTag.builder().name(s).build()));
        }
    }

    private Integer getSizeOrNull(String[] tagNames) {
        Integer size = null;
        if (tagNames != null) {
            size = tagNames.length;
        }
        return size;
    }

    private List<GiftAndTagDto> sortBySortType(String sort, List<GiftAndTagDto> list) {
        if (sort == null) {
            return list;
        } else {
            switch (sort) {
                case "name-ASC":
                    return list.stream()
                            .sorted(Comparator.comparing(GiftAndTagDto::getName))
                            .collect(Collectors.toList());
                case "name-DESC":
                    return list.stream()
                            .sorted(Comparator.comparing(GiftAndTagDto::getName)
                                    .reversed())
                            .collect(Collectors.toList());
                case "date-ASC":
                    return list.stream()
                            .sorted(Comparator.comparing(GiftAndTagDto::getCreateDate))
                            .collect(Collectors.toList());
                case "date-DESC":
                    return list.stream()
                            .sorted(Comparator.comparing(GiftAndTagDto::getCreateDate)
                                    .reversed())
                            .collect(Collectors.toList());
                case "ASC":
                    return list.stream()
                            .sorted(Comparator.comparing(GiftAndTagDto::getName)
                                    .thenComparing(GiftAndTagDto::getCreateDate))
                            .collect(Collectors.toList());
                case "DESC":
                    return list.stream()
                            .sorted(Comparator.comparing(GiftAndTagDto::getName)
                                    .thenComparing(GiftAndTagDto::getCreateDate)
                                    .reversed())
                            .collect(Collectors.toList());
                default:
                    return list;
            }
        }
    }

    /**
     * Send request for saving GiftAndTagDto
     *
     * @param giftAndTagDto - instance of GiftAndTagDto
     */
    @Transactional
    public void save(GiftAndTagDto giftAndTagDto) {
        Integer giftId = saveGiftAndGetGiftId(giftAndTagDto);
        List<Integer> tags = saveTagsAndGetTagsIdList(giftAndTagDto);
        if (!tags.isEmpty()) {
            tags.forEach(tagId -> giftsTagsService.save(giftId, tagId));
        }
    }

    private Integer saveGiftAndGetGiftId(GiftAndTagDto giftAndTagDto) {
        GiftCertificate giftCertificate = converterForGift.convertToEntity(giftAndTagDto);
        giftCertificateDao.save(giftCertificate);
        return giftCertificateDao.findId(giftCertificate);
    }

    private List<Integer> saveTagsAndGetTagsIdList(GiftAndTagDto giftAndTagDto) {
        List<Tag> tags = giftAndTagDto.getTags();
        if (tags.isEmpty()) {
            return new ArrayList<>();
        }
        tags.forEach(tagDao::save); //save all new tags
        return tags.stream()
                .map(tag -> tagDao.findTagIdByTagName(tag.getName())) // find id of all tags which associated with GiftCertificate
                .collect(Collectors.toList());
    }

    /**
     * Send request for deleting GiftAndTagDto
     *
     * @param id - Integer id
     */
    @Transactional
    public void deleteById(Integer id) {
        checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id));
        int size = giftCertificateDao.deleteById(id);
        checkForNotFoundException(size == 0, String.format("No Certificates Found to delete: id --> %d", id));
    }

    /**
     * Send request for updating only fields in GiftAndTagDto
     *
     * @param id      - Integer id
     * @param updates - Map<String, Object>, String - name of field, Object - value of field
     */
    @Transactional
    public void update(Map<String, Object> updates, Integer id) {
        checkForBadRequestException(id <= 0, "Invalid id");
        updates.forEach((k, v) -> {
            int size = giftCertificateDao.update(k, v, id);
            checkForNotFoundException(size == 0, String.format("No Certificates Found to update: id --> %d", id));
        });
    }
}
