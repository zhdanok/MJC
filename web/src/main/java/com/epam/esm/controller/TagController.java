package com.epam.esm.controller;

import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class TagController {

	private static final Integer NUMBER_OF_FIRST_PAGE = 1;

	private final TagService tagService;

	/**
	 * Send request for getting TagDtos with required page and limit
	 *
	 * @param page  - number of page with required limit (default value = 1)
	 * @param limit - count of Tags which need to view at page (default value = 2)
	 * @return CollectionModel with TagDto with pagination and links (HATEOAS)
	 */
	@GetMapping(value = "/tags", produces = {"application/hal+json"})
	public CollectionModel<TagDto> getTags(@RequestParam(value = "page", defaultValue = "1") Integer page,
										   @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
		List<TagDto> list = tagService.getTags(page, limit);
		for (final TagDto tagDto : list) {
			Integer tagDtoId = tagDto.getId();
			tagDto.add(linkTo(methodOn(TagController.class).getTagById(tagDtoId)).withSelfRel());
		}

		return getCollectionModelWithPagination(page, limit, list);
	}

	/**
	 * Send request for getting TagDto by Tag's id
	 * @param id - id of Tag which need to get
	 * @return ResponseEntity with TagDto and link (HATEOAS)
	 */
	@GetMapping(value = "/tags/{id}")
	public ResponseEntity<TagDto> getTagById(@PathVariable Integer id) {
		TagDto tagDto = tagService.getTagById(id);
		tagDto.add(linkTo(methodOn(TagController.class).getTagById(id)).withSelfRel());
		return new ResponseEntity<>(tagDto, HttpStatus.OK);
	}

	/**
	 * Send request for saving TagDto
	 * @param tagDto - Dto of Entity which need to save
	 * @return ResponseEntity with link of new Tag (or of existed Tag if it existed)
	 */
	@PostMapping(value = "/tags", consumes = {"application/json"}, produces = {"application/hal+json" })
	public ResponseEntity<Link> postTag(@RequestBody TagDto tagDto) {
		Integer id = tagService.save(tagDto);
		Link link = linkTo(methodOn(TagController.class).getTagById(id)).withSelfRel();
		return new ResponseEntity<>(link, HttpStatus.CREATED);
	}

	/**
	 * Send request for deleting Tag by Tag's Id
	 * @param id - id of Tag which need to delete
	 * @return ResponseEntity with HttpStatus ACCEPTED
	 */
	@DeleteMapping(value = "tags/{id}")
	public ResponseEntity<Void> deleteTagById(@PathVariable Integer id) {
		tagService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	/**
	 * Send request for getting the most widely used tag of a user with the highest cost
	 * of all orders
	 * @return ResponseEntity with TagDto and link (HATEOAS)
	 */
	@GetMapping(value = "/tags/pop")
	public ResponseEntity<TagDto> getMostPopularTagOfUserWithHighestCostOfOrder() {
		TagDto tagDto = tagService.getMostPopularTagOfUserWithHighestCostOfOrder();
		tagDto.add(linkTo(methodOn(TagController.class).getMostPopularTagOfUserWithHighestCostOfOrder()).withSelfRel());
		return new ResponseEntity<>(tagDto, HttpStatus.OK);
	}

	private CollectionModel<TagDto> getCollectionModelWithPagination(Integer page, Integer limit, List<TagDto> list) {
		Long sizeOfList = tagService.getSize();
		Integer lastPage = Math.toIntExact((sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit);
		Integer firstPage = NUMBER_OF_FIRST_PAGE;
		Integer nextPage = (page.equals(lastPage)) ? lastPage : page + 1;
		Integer prevPage = (page.equals(firstPage)) ? firstPage : page - 1;
		Link self = linkTo(methodOn(TagController.class).getTags(page, limit)).withSelfRel();
		Link next = linkTo(methodOn(TagController.class).getTags(nextPage, limit)).withRel("next");
		Link prev = linkTo(methodOn(TagController.class).getTags(prevPage, limit)).withRel("prev");
		Link first = linkTo(methodOn(TagController.class).getTags(firstPage, limit)).withRel("first");
		Link last = linkTo(methodOn(TagController.class).getTags(lastPage, limit)).withRel("last");
		return CollectionModel.of(list, first, prev, self, next, last);
	}

}
