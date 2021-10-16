package com.epam.esm.repository;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCertificateDao {

    void save(GiftCertificate giftCertificate);

    List<GiftAndTagDto> findById(Integer id);

    List<GiftAndTagDto> findByAnyParams(String tagName, String substr);

    int update(String key, Object value, Integer id);

    int deleteById(Integer id);

    Integer findId(GiftCertificate giftCertificate);
}
