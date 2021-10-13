package com.epam.esm.repository;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.mapper.GiftAndTagDtoRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(GiftCertificate giftCertificate) {
        String sql = "INSERT INTO gift_certificate(name, description, price, duration, create_date, " +
                "last_update_date)  VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, giftCertificate.getName(), giftCertificate.getDescription(),
                giftCertificate.getPrice(), giftCertificate.getDuration(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));

    }

    @Override
    public List<GiftAndTagDto> findAll() {
        String sql = "SELECT gc.*, JSON_ARRAYAGG(t.name) AS 'tags'\n" +
                "FROM gift_certificate AS gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.id = gt.tag_id\n" +
                "GROUP BY gc.id";
        return jdbcTemplate.query(sql, new GiftAndTagDtoRowMapper());
    }

    @Override
    public List<GiftAndTagDto> findByTagName(String tagName) {
        String sql = "SELECT gc.*, JSON_ARRAYAGG(t.name) AS 'tags'\n" +
                "FROM gift_certificate AS gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.id = gt.tag_id\n" +
                "WHERE t.name = ?" +
                "GROUP BY gc.id";
        return jdbcTemplate.query(sql, new GiftAndTagDtoRowMapper(), tagName);
    }

    @Override
    public List<GiftAndTagDto> findByNameOrDescriptionContaining(String substr) {
        String sql = "SELECT gc.*, JSON_ARRAYAGG(t.name) AS 'tags'\n" +
                "FROM gift_certificate AS gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.id = gt.tag_id\n" +
                "WHERE gc.name LIKE CONCAT('%', ?, '%') OR gc.description LIKE CONCAT('%', ?, '%')" +
                "GROUP BY gc.id";
        return jdbcTemplate.query(sql, new GiftAndTagDtoRowMapper(), substr, substr);
    }

    @Override
    public Integer findId(GiftCertificate gc) {
        String sql = "SELECT gc.id FROM gift_certificate gc\n" +
                "WHERE gc.name = ? AND gc.description = ? AND gc.price = ? AND gc.duration = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, gc.getName(), gc.getDescription(), gc.getPrice(), gc.getDuration());
    }

    @Override
    public int update(String key, Object value, Integer id) {
        String sql = String.format("UPDATE gift_certificate SET %s", key) + "= ?, last_update_date = ? WHERE id = ?";
        return jdbcTemplate.update(sql, value, Timestamp.from(Instant.now()), id);
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM gift_certificate where id = ?";
        return jdbcTemplate.update(sql, id);
    }


}
