package com.epam.esm.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface GiftsTagsDao {

    int save(Integer giftId, Integer tagId);
}
