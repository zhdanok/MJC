package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface GiftCertificateDao {

    void save(GiftCertificate giftCertificate);

    GiftCertificate findById(Integer id);

    List<GiftCertificate> findByAnyParams(Long size, String substr, Integer skip, Integer limit);

    int update(Map<String, Object> updates, Integer id, Instant now);

    int deleteById(Integer id);

    Integer findId(GiftCertificate giftCertificate);

    Double findPriceById(Integer id);

    String findNameById(Integer giftId);

    Long findSize(Long size, String substr);
}
