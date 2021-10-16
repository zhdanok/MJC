package com.epam.esm.mapper;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.Tag;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GiftAndTagExtractor implements ResultSetExtractor<List<GiftAndTagDto>> {

    @Override
    public List<GiftAndTagDto> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<GiftAndTagDto, List<Tag>> data = new LinkedHashMap<>();
        while (rs.next()) {
            GiftAndTagDto dto = GiftAndTagDto.builder()
                    .id(rs.getInt("gift_id"))
                    .name(rs.getString("gift_name"))
                    .description(rs.getString("description"))
                    .price(rs.getDouble("price"))
                    .duration(rs.getInt("duration"))
                    .createDate(rs.getTimestamp("create_date").toInstant())
                    .lastUpdateDate(rs.getTimestamp("last_update_date").toInstant())
                    .build();
            data.putIfAbsent(dto, new ArrayList<>());
            Tag tag = Tag.builder()
                    .id(rs.getInt("tag_id"))
                    .name(rs.getString("tag_name"))
                    .build();
            data.get(dto).add(tag);
        }
        for (Map.Entry<GiftAndTagDto, List<Tag>> entry : data.entrySet()) {
            entry.getKey().setTags(entry.getValue());
        }
        return new ArrayList<>(data.keySet());
    }
}
