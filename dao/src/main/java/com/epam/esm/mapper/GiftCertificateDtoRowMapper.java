package com.epam.esm.mapper;


import com.epam.esm.dto.GiftCertificateDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GiftCertificateDtoRowMapper implements RowMapper<GiftCertificateDto> {

    @Override
    public GiftCertificateDto mapRow(ResultSet rs, int i) throws SQLException {
        GiftCertificateDto giftCertificate = new GiftCertificateDto();
        giftCertificate.setId(rs.getInt("id"));
        giftCertificate.setName(rs.getString("name"));
        giftCertificate.setDescription(rs.getString("description"));
        giftCertificate.setPrice(rs.getDouble("price"));
        giftCertificate.setDuration(rs.getInt("duration"));
        giftCertificate.setCreateDate(rs.getTimestamp("create_date").toInstant());
        giftCertificate.setLastUpdateDate(rs.getTimestamp("last_update_date").toInstant());

        return giftCertificate;

    }


}

