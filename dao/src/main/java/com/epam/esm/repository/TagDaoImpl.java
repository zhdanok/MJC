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
    public int save(Tag tag) {
        String sql = "INSERT IGNORE INTO tag (name) VALUES ( ? )";
        return jdbcTemplate.update(sql, tag.getName());
    }

    @Override
    public List<TagDto> findAll() {
        String sql = "SELECT * FROM tag";
        return jdbcTemplate.query(sql, new TagDtoRowMapper());
    }

    @Override
    public List<TagDto> findById(Integer id) {
        String sql = "SELECT * FROM tag WHERE id = ?";
        return jdbcTemplate.query(sql, new TagDtoRowMapper(), id);
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE  FROM tag WHERE id = ?";
        return jdbcTemplate.update(sql, id);

    }

    @Override
    public Integer findByTagName(String tagName) {
        String sql = "SELECT id FROM tag WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, tagName);
    }
}
