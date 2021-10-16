package com.epam.esm.repository;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
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
    public void save(Tag tag) {
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
}
