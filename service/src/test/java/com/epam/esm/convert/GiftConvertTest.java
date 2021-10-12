package com.epam.esm.convert;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;

class GiftConvertTest {

    @Autowired
    Convert<GiftCertificate, GiftAndTagDto> convert;

}