package com.epam.esm.controller;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.service.GiftCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GiftCertificateController {

    private final GiftCertificateService giftCertificateService;

    @GetMapping(value = "/gifts")
    public ResponseEntity<List<GiftAndTagDto>> getGiftCertificates() {
        List<GiftAndTagDto> list = giftCertificateService.getCertificates();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/gifts/{id}")
    public ResponseEntity<List<GiftAndTagDto>> getGiftCertificateById(@PathVariable Integer id) {
        List<GiftAndTagDto> list = giftCertificateService.getCertificateById(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/gifts", params = {"tag"})
    public ResponseEntity<List<GiftAndTagDto>> getGiftCertificatesByTagName(@RequestParam(value = "tag") String tagName) {
        List<GiftAndTagDto> list = giftCertificateService.getCertificatesByTagName(tagName);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/gifts", params = {"substr"})
    public ResponseEntity<List<GiftAndTagDto>> getGiftCertificatesBySubstr(@RequestParam(value = "substr") String substr) {
        List<GiftAndTagDto> list = giftCertificateService.getCertificatesBySubstr(substr);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/gifts", params = {"sort"})
    public ResponseEntity<List<GiftAndTagDto>> getCertificateByConjunctionParams(@RequestParam(value = "sort") String sort) {
        List<GiftAndTagDto> list = giftCertificateService.sortCertificates(sort);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/gifts", params = {"tag", "substr", "sort"})
    public ResponseEntity<List<GiftAndTagDto>> getGiftCertificatesByAnyParams(@RequestParam(value = "tag", required = false) String tagName,
                                                                              @RequestParam(value = "substr", required = false) String substr,
                                                                              @RequestParam(value = "sort",required = false) String sort) {
        List<GiftAndTagDto> list = giftCertificateService.getCertificatesByAnyParams(tagName, substr, sort);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/gifts", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<GiftCertificateDto> postCertificate(@RequestBody GiftAndTagDto GiftAndTagDto) {
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
