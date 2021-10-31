package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.SearchTags;
import com.epam.esm.repository.GiftCertificateDao;
import com.epam.esm.repository.SearchTagDao;
import com.epam.esm.repository.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.esm.util.checkUtil.checkForBadRequestException;
import static com.epam.esm.util.checkUtil.checkForNotFoundException;

@Service
@RequiredArgsConstructor
public class GiftCertificateService {

	private final TagDao tagDao;

	private final SearchTagDao searchTagDao;

	private final GiftCertificateDao giftCertificateDao;

	private final Converter<GiftCertificate, GiftAndTagDto> converterForGift;

	public static final String ERR_CODE_GIFT = "01";

	/**
	 * Send request for getting Certificate by id
	 *
	 * @param id - Integer
	 * @return Dto which contains Certificate with Tags
	 */
	public GiftAndTagDto getCertificateById(Integer id) {
		GiftCertificate gc = giftCertificateDao.findById(id);
		checkForNotFoundException(gc == null, String.format("Gift Certificate with id '%d' not found", id),
				ERR_CODE_GIFT);
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
	 * @param sort     - String - style of sorting (optional): name-asc/name-desc - by Tag's
	 *                 name asc/desc date-asc/date-desc - by Date of creation asc/desc
	 *                 name-date-asc/name-date-desc - by Tag's name and then by Date of creating asc/desc
	 * @return List of Dto which contains Certificates and Tags
	 */
	@Transactional
	public List<GiftAndTagDto> getCertificatesByAnyParams(String[] tagNames, String substr, String sort, Integer page,
														  Integer limit) {
		Long size = ifExistThenSaveSearchTagsAndReturnSize(tagNames);
		Integer skip = (page - 1) * limit;
		List<GiftCertificate> list = giftCertificateDao.findByAnyParams(size, substr, skip, limit, sort);
		checkForNotFoundException(list.isEmpty(), "Gift Certificates with this parameters not found", ERR_CODE_GIFT);
		return list.stream().map(converterForGift::convertToDto).collect(Collectors.toList());
	}

	/**
	 * Save all SearchTags in database if their exist, return count of Tags
	 * @param tagNames - Array of Tag's name(optional, can be one or several)
	 * @return size - count of Tags
	 */
	@Transactional
	public Long ifExistThenSaveSearchTagsAndReturnSize(String[] tagNames) {
		Long size = null;
		if (tagNames != null) {
			size = Long.valueOf(tagNames.length);
			searchTagDao.clear();
			Arrays.stream(tagNames).forEach(s -> searchTagDao.save(SearchTags.builder().name(s).build()));
		}
		return size;
	}

	/**
	 * Send request for saving GiftAndTagDto
	 * @param dto - instance of GiftAndTagDto
	 * @return Integer - id of the entity that was saved
	 */
	@Transactional
	public Integer save(GiftAndTagDto dto) {
		GiftCertificate giftCertificate = prepareEntityForSave(dto);
		Integer id = giftCertificateDao.findGiftIdByGiftName(giftCertificate.getName());
		if (id == null) {
			giftCertificateDao.save(giftCertificate);
			return giftCertificateDao.findId(giftCertificate);
		}
		return id;
	}

	private GiftCertificate prepareEntityForSave(GiftAndTagDto dto) {
		List<TagDto> tags = dto.getTags();
		tags.stream().forEach(tag -> tag.setId(tagDao.findTagIdByTagName(tag.getName())));
		dto.setTags(tags);
		dto.setCreateDate(Instant.now());
		dto.setLastUpdateDate(Instant.now());
		GiftCertificate giftCertificate = converterForGift.convertToEntity(dto);
		return giftCertificate;
	}

	/**
	 * Send request for deleting GiftAndTagDto
	 * @param id - Integer id
	 */
	@Transactional
	public void deleteById(Integer id) {
		checkForBadRequestException(id <= 0, String.format("Invalid id --> %d", id), ERR_CODE_GIFT);
		int size = giftCertificateDao.deleteById(id);
		checkForNotFoundException(size == 0, String.format("No Certificates Found to delete: id --> %d", id),
				ERR_CODE_GIFT);
	}

	/**
	 * Send request for updating only fields in GiftAndTagDto
	 * @param id - Integer id
	 * @param updates - Map<String, Object>, String - name of field, Object - value of
	 * field
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
	 * @param tagNames - Array of Tag's name(optional, can be one or several)
	 * @return size - count of Results which match the search parameters
	 */
	public Long getSize(String[] tagNames, String substr) {
		Long size = (tagNames != null) ? Long.valueOf(tagNames.length) : null;
		return giftCertificateDao.findSize(size, substr);
	}

}
