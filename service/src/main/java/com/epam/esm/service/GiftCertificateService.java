package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.GiftCertificateDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftCertificateService {

    private final TagService tagService;

    private final GiftsTagsService giftsTagsService;

    private final GiftCertificateDao giftCertificateDao;

    private final Converter<GiftCertificate, GiftAndTagDto> converter;

    /**
     * Send request for getting all Certificates with their tags
     *
     * @return List of Dto which contains Certificates and arrays of tag names *
     */
    public List<GiftAndTagDto> getCertificates() {
        List<GiftAndTagDto> list = giftCertificateDao.findAll();
        checkForNotFoundException(list.isEmpty(), "Gift Certificates not found");
        return list;
    }


    /**
     * Send request for getting all Certificates by one Tag's name
     *
     * @param tagName - String
     * @return List of Dto which contains Certificates with one Tag *
     */
    public List<GiftAndTagDto> getCertificatesByTagName(String tagName) {
        List<GiftAndTagDto> list = giftCertificateDao.findByTagName(tagName);
        checkForNotFoundException(list.isEmpty(), String.format("Gift Certificates with tag '%s' not found", tagName));
        return list;
    }

    /**
     * Send request for getting all Certificates which contain substring in name or description
     *
     * @param substr - String
     * @return List of Dto which contains Certificates which contain substring *
     */
    public List<GiftAndTagDto> getCertificatesBySubstr(String substr) {
        List<GiftAndTagDto> list = giftCertificateDao.findByNameOrDescriptionContaining(substr);
        checkForNotFoundException(list.isEmpty(), String.format("Gift Certificates with substring '%s' into name or description not found", substr));
        return list;
    }

    /**
     * Send request for getting all sorted Certificates with their tags
     *
     * @param sort - String. Type of sort.
     * @return List of sorted Dto *
     */
    public List<GiftAndTagDto> sortCertificates(String sort) {
        List<GiftAndTagDto> list = giftCertificateDao.findAll();
        checkForNotFoundException(list.isEmpty(), "Gift Certificates not found");
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
            tags.stream().forEach(tagId -> giftsTagsService.save(giftId, tagId));
        }
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
        boolean isValidRequest = updates.keySet()
                .stream()
                .allMatch(this::isValidRequestParam);
        checkForBadRequestException((!isValidRequest) || (id <= 0), "Invalid request params or id");
        updates.forEach((k, v) -> {
            int size = giftCertificateDao.update(k, v, id);
            checkForNotFoundException(size == 0, String.format("No Certificates Found to update: id --> %d", id));
        });
    }

    private Integer saveGiftAndGetGiftId(GiftAndTagDto giftAndTagDto) {
        GiftCertificate giftCertificate = converter.convertToEntity(giftAndTagDto);
        giftCertificateDao.save(giftCertificate);
        return giftCertificateDao.findId(giftCertificate);
    }

    private List<Integer> saveTagsAndGetTagsIdList(GiftAndTagDto giftAndTagDto) {
        List<String> tags = giftAndTagDto.getTags();
        if (tags.isEmpty()) {
            return new ArrayList<>();
        }
        tags.stream()
                .map(TagDto::new)
                .forEach(tagService::save);
        return tagService.findTagByName(giftAndTagDto.getTags());
    }

    private boolean isValidRequestParam(String key) {
        Field[] fields = GiftCertificate.class.getDeclaredFields();
        List<String> names = Arrays.stream(fields)
                .map(Field::getName)
                .collect(Collectors.toList());
        return names.contains(key);
    }

    private void checkForNotFoundException(boolean empty, String s) {
        if (empty) {
            throw new ResourceNotFoundException(s);
        }
    }

    private void checkForBadRequestException(boolean b, String s) {
        if (b) {
            throw new BadRequestException(s);
        }
    }

}
