package com.epam.esm.repository;

import com.epam.esm.entity.SearchTag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchTagDaoImpl implements SearchTagDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(SearchTag searchTag) {
        String sql = "INSERT INTO searchtags (stag_name) VALUES ( ? )";
        jdbcTemplate.update(sql, searchTag.getName());
    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE searchtags ";
        jdbcTemplate.update(sql);
    }
}
