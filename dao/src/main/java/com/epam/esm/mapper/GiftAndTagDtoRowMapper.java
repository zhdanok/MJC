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
        List<String> tags = Arrays.stream(
                        rs.getString("tags") //get String like "[a, b, c]"
                                .replaceAll("\\p{P}", "") //removed all punctuation marks from String
                                .split(" ")) // split with " " and converted String to String[]
                .collect(Collectors.toList()); // convert String[] to List and return
        giftAndTagDto.setTags(tags);

        return giftAndTagDto;

    }


}
