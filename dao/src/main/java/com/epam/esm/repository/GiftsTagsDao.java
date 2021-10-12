package com.epam.esm.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface GiftsTagsDao {

    void save(Integer giftId, Integer tagId);
}
