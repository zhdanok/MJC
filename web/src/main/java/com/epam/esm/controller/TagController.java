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

    private final TagService tagService;

    @GetMapping(value = "/tags", produces = {"application/hal+json"})
    public CollectionModel<TagDto> getTags() {
        List<TagDto> list = tagService.getTags();
        for (final TagDto tagDto : list) {
            Integer tagDtoId = tagDto.getId();
            tagDto.add(linkTo(methodOn(TagController.class).getTagById(tagDtoId)).withSelfRel());
        }
        Link link = linkTo(methodOn(TagController.class).getTags()).withSelfRel();
        return CollectionModel.of(list, link);
    }

    @GetMapping(value = "/tags/{id}")
    public ResponseEntity<TagDto> getTagById(@PathVariable Integer id) {
        TagDto tagDto = tagService.getTagById(id);
        tagDto.add(linkTo(methodOn(TagController.class).getTagById(id)).withSelfRel());
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }

    @PostMapping(value = "/tags", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Void> postTag(@RequestBody TagDto tagDto) {
        tagService.save(tagDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "tags/{id}")
    public ResponseEntity<Void> deleteTagById(@PathVariable Integer id) {
        tagService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/tags/pop")
    public ResponseEntity<TagDto> getMostPopularTagOfUserWithHighestCostOfOrder() {
        TagDto tagDto = tagService.getMostPopularTagOfUserWithHighestCostOfOrder();
        tagDto.add(linkTo(methodOn(TagController.class).getMostPopularTagOfUserWithHighestCostOfOrder()).withSelfRel());
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }
}
