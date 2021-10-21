package com.epam.esm.repository;

import com.epam.esm.dto.TagDto;
import com.epam.esm.mapper.TagDtoRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagDaoImpl implements TagDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(TagDto tag) {
        String sql = "INSERT IGNORE INTO tag (tag_name) VALUES ( ? )";
        jdbcTemplate.update(sql, tag.getName());
    }

    @Override
    public List<TagDto> findAll() {
        String sql = "SELECT * FROM tag";
        return jdbcTemplate.query(sql, new TagDtoRowMapper());
    }

    @Override
    public List<TagDto> findById(Integer id) {
        String sql = "SELECT * FROM tag WHERE tag_id = ?";
        return jdbcTemplate.query(sql, new TagDtoRowMapper(), id);
    }

    @Override
    public Integer findTagIdByTagName(String name) {
        String sql = "SELECT tag_id FROM tag WHERE tag_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE  FROM tag WHERE tag_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<TagDto> findMostPopularTagOfUserWithHighestCostOfOrder() {
        String sql = "SELECT t.* from tag t\n" +
                "JOIN gifts_tags gt on t.tag_id = gt.tag_id\n" +
                "JOIN gift_certificate gc on gc.gift_id = gt.gift_id\n" +
                "WHERE gc.gift_id IN (\n" +
                "SELECT o.gift_id from `order` o where o.user_id = (" +
                "SELECT o.user_id from `order` o group by o.user_id order by SUM(o.cost) DESC limit 1))\n" +
                "group by t.tag_id order by COUNT(t.tag_name) DESC limit 1";
        return jdbcTemplate.query(sql, new TagDtoRowMapper());
    }
}
