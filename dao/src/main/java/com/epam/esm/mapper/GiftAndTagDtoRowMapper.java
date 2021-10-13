package com.epam.esm.mapper;

import com.epam.esm.dto.GiftAndTagDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiftAndTagDtoRowMapper implements RowMapper<GiftAndTagDto> {

    @Override
    public GiftAndTagDto mapRow(ResultSet rs, int i) throws SQLException {
        GiftAndTagDto giftAndTagDto = new GiftAndTagDto();
        giftAndTagDto.setId(rs.getInt("id"));
        giftAndTagDto.setName(rs.getString("name"));
        giftAndTagDto.setDescription(rs.getString("description"));
        giftAndTagDto.setPrice(rs.getDouble("price"));
        giftAndTagDto.setDuration(rs.getInt("duration"));
        giftAndTagDto.setCreateDate(rs.getTimestamp("create_date").toInstant());
        giftAndTagDto.setLastUpdateDate(rs.getTimestamp("last_update_date").toInstant());
        //Converted string like "[a, b, c]" into List": removed all punctuation marks, split with " ", collected to List
        List<String> tags = Arrays.stream(rs.getString("tags").replaceAll("\\p{P}", "").split(" ")).collect(Collectors.toList());
        giftAndTagDto.setTags(tags);

        return giftAndTagDto;

    }


}