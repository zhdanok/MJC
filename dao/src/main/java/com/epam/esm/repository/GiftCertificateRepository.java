package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiftCertificateRepository extends PagingAndSortingRepository<GiftCertificate, Integer> {

    Optional<GiftCertificate> findGiftCertificateByName(String name);

}
