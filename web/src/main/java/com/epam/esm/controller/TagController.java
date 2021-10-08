package com.epam.esm.controller;

import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping(value = "/tags")
    public ResponseEntity<List<TagDto>> getTags() {
        List<TagDto> list = tagService.getTags();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/tags/{id}")
    public ResponseEntity<List<TagDto>> getTagById(@PathVariable Integer id) {
        List<TagDto> list = tagService.getTagById(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/tags", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<TagDto> postTag(@RequestBody TagDto tagDto) {
        tagService.save(tagDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "tags/{id}")
    public ResponseEntity<Void> deleteTagById(@PathVariable Integer id) {
        tagService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
