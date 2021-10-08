package com.epam.esm.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GiftsTagsDaoImpl implements GiftsTagsDao{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(Integer giftId, Integer tagId) {
        String sql = "INSERT INTO gifts_tags(gift_id, tag_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, giftId, tagId);
    }
}
