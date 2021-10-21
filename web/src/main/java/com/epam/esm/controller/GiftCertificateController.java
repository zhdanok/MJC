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

    private final GiftCertificateService giftCertificateService;

    @GetMapping(value = "/gifts/{id}", produces = {"application/hal+json"})
    public ResponseEntity<GiftAndTagDto> getGiftCertificateById(@PathVariable Integer id) {
        GiftAndTagDto dto = giftCertificateService.getCertificateById(id);
        dto.add(linkTo(methodOn(GiftCertificateController.class).getGiftCertificateById(id)).withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/gifts", produces = {"application/hal+json"})
    public CollectionModel<GiftAndTagDto> getGiftCertificatesByAnyParams(@RequestParam(value = "tag", required = false) String[] tagNames,
                                                                         @RequestParam(value = "substr", required = false) String substr,
                                                                         @RequestParam(value = "sort", required = false) String sort) {
        List<GiftAndTagDto> list = giftCertificateService.getCertificatesByAnyParams(tagNames, substr, sort);
        for (GiftAndTagDto dto : list) {
            dto.add(linkTo(methodOn(GiftCertificateController.class).getGiftCertificateById(dto.getId())).withSelfRel());
            for (TagDto tagDto : dto.getTags()) {
                tagDto.add(linkTo(methodOn(TagController.class).getTagById(tagDto.getId())).withSelfRel());
            }
        }
        Link link = linkTo(methodOn(GiftCertificateController.class).getGiftCertificatesByAnyParams(tagNames, substr, sort)).withSelfRel();
        return CollectionModel.of(list, link);
    }

    @PostMapping(value = "/gifts", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Void> postCertificate(@RequestBody GiftAndTagDto GiftAndTagDto) {
        giftCertificateService.save(GiftAndTagDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping(value = "/gifts/{id}", consumes = {"application/json"})
    public ResponseEntity<?> updateCertificates(@RequestBody Map<String, Object> updates,
                                                @PathVariable Integer id) {
        giftCertificateService.update(updates, id);
        return ResponseEntity.ok("resource updated");
    }

    @DeleteMapping(value = "gifts/{id}")
    public ResponseEntity<Void> deleteCertificateById(@PathVariable Integer id) {
        giftCertificateService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
