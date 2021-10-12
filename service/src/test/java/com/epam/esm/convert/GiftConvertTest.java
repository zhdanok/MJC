package com.epam.esm.convert;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GiftConvertTest {

    @Autowired
    Convert<GiftCertificate, GiftAndTagDto> convert;

    @Test
    void convertToEntity() {

    }
}