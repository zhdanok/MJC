package com.epam.esm.controller;

import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class TagController {

    private static final Integer NUMBER_OF_FIRST_PAGE = 0;

    private final TagService tagService;

    /**
     * Send request for getting TagDtos with required page and limit
     *
     * @param page - number of page with required limit (default value = 0)
     * @param size - count of Tags which need to view at page (default value = 10)
     * @return CollectionModel with TagDto with pagination and links (HATEOAS)
     */
    @GetMapping(value = "/tags", produces = {"application/hal+json"})
    public CollectionModel<TagDto> getTags(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<TagDto> pages = tagService.getTags(PageRequest.of(page, size, Sort.by("id").ascending()));
        for (final TagDto tag : pages.getContent()) {
            Integer tagId = tag.getId();
            tag.add(linkTo(methodOn(TagController.class).getTagById(tagId)).withSelfRel());
        }
        return getCollectionModelWithPagination(pages);
    }

    /**
     * Send request for getting TagDto by Tag's id
     *
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
     *
     * @param tagDto - Dto of Entity which need to save
     * @return ResponseEntity with link of new Tag (or of existed Tag if it existed)
     */
    @PostMapping(value = "/tags", consumes = {"application/json"}, produces = {"application/hal+json"})
    public ResponseEntity<Link> postTag(@RequestBody TagDto tagDto) {
        Integer id = tagService.save(tagDto);
        Link link = linkTo(methodOn(TagController.class).getTagById(id)).withSelfRel();
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }

    /**
     * Send request for deleting Tag by Tag's Id
     *
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
     *
     * @return ResponseEntity with TagDto and link (HATEOAS)
     */
    @GetMapping(value = "/tags/pop")
    public ResponseEntity<TagDto> getMostPopularTagOfUserWithHighestCostOfOrder() {
        TagDto tagDto = tagService.getMostPopularTagOfUserWithHighestCostOfOrder();
        tagDto.add(linkTo(methodOn(TagController.class).getMostPopularTagOfUserWithHighestCostOfOrder()).withSelfRel());
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }

    private CollectionModel<TagDto> getCollectionModelWithPagination(Page<TagDto> pages) {
        Integer lastPage = pages.getTotalPages() - 1;
        Integer firstPage = NUMBER_OF_FIRST_PAGE;
        Integer nextPage = pages.nextOrLastPageable().getPageNumber();
        Integer prevPage = pages.previousOrFirstPageable().getPageNumber();
        Link self = linkTo(methodOn(TagController.class).getTags(pages.getNumber(), pages.getSize())).withSelfRel();
        Link next = linkTo(methodOn(TagController.class).getTags(nextPage, pages.getSize())).withRel("next");
        Link prev = linkTo(methodOn(TagController.class).getTags(prevPage, pages.getSize())).withRel("prev");
        Link first = linkTo(methodOn(TagController.class).getTags(firstPage, pages.getSize())).withRel("first");
        Link last = linkTo(methodOn(TagController.class).getTags(lastPage, pages.getSize())).withRel("last");
        return CollectionModel.of(pages, first, prev, self, next, last);
    }

}
