package com.epam.esm.mapper;

import com.epam.esm.dto.TagDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagDtoRowMapper implements RowMapper<TagDto> {

    @Override
    public TagDto mapRow(ResultSet rs, int i) throws SQLException {
        TagDto tag = new TagDto();
        tag.setId(rs.getInt("tag_id"));
        tag.setName(rs.getString("tag_name"));
        return tag;
    }
}
