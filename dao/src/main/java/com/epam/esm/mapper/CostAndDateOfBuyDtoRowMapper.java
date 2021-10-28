package com.epam.esm.mapper;

import com.epam.esm.dto.CostAndDateOfBuyDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CostAndDateOfBuyDtoRowMapper implements RowMapper<CostAndDateOfBuyDto> {

    @Override
    public CostAndDateOfBuyDto mapRow(ResultSet rs, int i) throws SQLException {
        CostAndDateOfBuyDto dto = CostAndDateOfBuyDto.builder().cost(rs.getDouble("cost"))
                .dateOfBuy(rs.getTimestamp("date_of_buy").toInstant()).build();
        return dto;
    }

}
