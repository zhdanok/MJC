package com.epam.esm.controller;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.service.GiftCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class GiftCertificateController {

	private static final Integer NUMBER_OF_FIRST_PAGE = 1;

	private final GiftCertificateService giftCertificateService;

	/**
	 * Send request for getting GiftAndTagDto by GiftCertificate's id
	 * @param id - id of GiftCertificate which need to get
	 * @return ResponseEntity with UserDto and link (HATEOAS)
	 */
	@GetMapping(value = "/gifts/{id}", produces = {"application/hal+json"})
	public ResponseEntity<GiftAndTagDto> getGiftCertificateById(@PathVariable Integer id) {
		GiftAndTagDto dto = giftCertificateService.getCertificateById(id);
		for (TagDto tagDto : dto.getTags()) {
			tagDto.add(linkTo(methodOn(TagController.class).getTagById(tagDto.getId())).withSelfRel());
		}
		dto.add(linkTo(methodOn(GiftCertificateController.class).getGiftCertificateById(id)).withSelfRel());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/**
	 * Send request for getting GiftAndTagDto with required page and limit
	 *
	 * @param tagNames - Array of Tag's name(optional, can be one or several)
	 * @param page     - Number of Page (optional)
	 * @param limit    - Limit of results at Page (optional)
	 * @param substr   - String - substring that can be contained into name or description
	 *                 (optional)
	 * @param sort     - String - style of sorting (optional): name-asc/name-desc - by Tag's
	 *                 name asc/desc date-asc/date-desc - by Date of creation asc/desc
	 *                 name-date-asc/name-date-desc - by Tag's name and then by Date of creating asc/desc
	 * @return CollectionModel with GiftAndTagDto with pagination and links (HATEOAS)
	 */
	@GetMapping(value = "/gifts", produces = { "application/hal+json" })
	public CollectionModel<GiftAndTagDto> getGiftCertificatesByAnyParams(
			@RequestParam(value = "tag", required = false) String[] tagNames,
			@RequestParam(value = "substr", required = false) String substr,
			@RequestParam(value = "sort_by", required = false) String sort,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "2") Integer limit) {
		List<GiftAndTagDto> list = giftCertificateService.getCertificatesByAnyParams(tagNames, substr, sort, page,
				limit);
		for (GiftAndTagDto dto : list) {
			dto.add(linkTo(methodOn(GiftCertificateController.class).getGiftCertificateById(dto.getId()))
					.withSelfRel());
			for (TagDto tagDto : dto.getTags()) {
				tagDto.add(linkTo(methodOn(TagController.class).getTagById(tagDto.getId())).withSelfRel());
			}
		}
		return getCollectionModelWithPagination(tagNames, substr, sort, page, limit, list);
	}

	/**
	 * Send request for saving GiftCertificate with Tags
	 * @param giftAndTagDto - Dto of Entity which need to save
	 * @return ResponseEntity with link of new GiftCertificate with Tags (or of existed
	 * User if it existed)
	 */
	@PostMapping(value = "/gifts", consumes = { "application/json" }, produces = { "application/hal+json" })
	public ResponseEntity<Link> postCertificate(@RequestBody GiftAndTagDto giftAndTagDto) {
		Integer id = giftCertificateService.save(giftAndTagDto);
		Link link = linkTo(methodOn(GiftCertificateController.class).getGiftCertificateById(id)).withSelfRel();
		return new ResponseEntity<>(link, HttpStatus.CREATED);
	}

	/**
	 * Send request for updating only fields in GiftAndTagDto
	 * @param id - Integer id
	 * @param updates - Map<String, Object>, String - name of field, Object - value of
	 * field
	 */
	@PatchMapping(value = "/gifts/{id}", consumes = { "application/json" })
	public ResponseEntity<?> updateCertificates(@RequestBody Map<String, Object> updates, @PathVariable Integer id) {
		giftCertificateService.update(updates, id);
		return ResponseEntity.ok("resource updated");
	}

	/**
	 * Send request for deleting GiftAndTagDto
	 * @param id - Integer id
	 */
	@DeleteMapping(value = "gifts/{id}")
	public ResponseEntity<Void> deleteCertificateById(@PathVariable Integer id) {
		giftCertificateService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	private CollectionModel<GiftAndTagDto> getCollectionModelWithPagination(String[] tagNames, String substr,
																			String sort, Integer page, Integer limit, List<GiftAndTagDto> list) {
		Long sizeOfList = giftCertificateService.getSize(tagNames, substr);
		Integer lastPage = Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
		Integer firstPage = NUMBER_OF_FIRST_PAGE;
		Integer nextPage = (page.equals(lastPage)) ? lastPage : page + 1;
		Integer prevPage = (page.equals(firstPage)) ? firstPage : page - 1;
		Link self = linkTo(methodOn(GiftCertificateController.class).getGiftCertificatesByAnyParams(tagNames, substr,
				sort, page, limit)).withSelfRel();
		Link next = linkTo(methodOn(GiftCertificateController.class).getGiftCertificatesByAnyParams(tagNames, substr,
				sort, nextPage, limit)).withRel("next");
		Link prev = linkTo(methodOn(GiftCertificateController.class).getGiftCertificatesByAnyParams(tagNames, substr,
				sort, prevPage, limit)).withRel("prev");
		Link first = linkTo(methodOn(GiftCertificateController.class).getGiftCertificatesByAnyParams(tagNames, substr,
				sort, firstPage, limit)).withRel("first");
		Link last = linkTo(methodOn(GiftCertificateController.class).getGiftCertificatesByAnyParams(tagNames, substr,
				sort, lastPage, limit)).withRel("last");
		return CollectionModel.of(list, first, prev, self, next, last);
	}

}
