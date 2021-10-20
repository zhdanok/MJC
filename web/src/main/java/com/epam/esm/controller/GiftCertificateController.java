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

    @GetMapping(value = "/gifts/{id}")
    public ResponseEntity<List<GiftAndTagDto>> getGiftCertificateById(@PathVariable Integer id) {
        List<GiftAndTagDto> list = giftCertificateService.getCertificateById(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/gifts")
    public ResponseEntity<List<GiftAndTagDto>> getGiftCertificatesByAnyParams(@RequestParam(value = "tag", required = false) String[] tagNames,
                                                                              @RequestParam(value = "substr", required = false) String substr,
                                                                              @RequestParam(value = "sort", required = false) String sort) {
        List<GiftAndTagDto> list = giftCertificateService.getCertificatesByAnyParams(tagNames, substr, sort);
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
