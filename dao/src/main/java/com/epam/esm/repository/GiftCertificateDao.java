package com.epam.esm.repository;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GiftCertificateDao {

    @Transactional
    void save(GiftCertificate giftCertificate);

    List<GiftAndTagDto> findAll();

    @Transactional
    int update(String key, Object value, Integer id);

    @Transactional
    int deleteById(Integer id);

    List<GiftAndTagDto> findByTagName(String tagName);

    List<GiftAndTagDto> findByNameOrDescriptionContaining(String substr);


    Integer findId(GiftCertificate giftCertificate);
}

