package com.epam.esm.repository;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.mapper.GiftAndTagExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    private final GiftAndTagExtractor giftAndTagExtractor;

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void save(GiftCertificate gc) {
        String sql = "INSERT INTO gift_certificate(gift_name, description, price, duration, create_date, " +
                "last_update_date)  VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, gc.getName(), gc.getDescription(),
                gc.getPrice(), gc.getDuration(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));

    }

    @Override
    public List<GiftAndTagDto> findAll() {
        String sql = "SELECT gc.*, t.*\n" +
                "FROM gift_certificate as gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.tag_id = gt.tag_id\n" +
                "ORDER BY gc.gift_id";
        return jdbcTemplate.query(sql, giftAndTagExtractor);
    }

    @Override
    public List<GiftAndTagDto> findById(Integer id) {
        String sql = "SELECT gc.*, t.*\n" +
                "FROM gift_certificate as gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.tag_id = gt.tag_id\n" +
                "WHERE gc.gift_id = ?";
        return jdbcTemplate.query(sql, giftAndTagExtractor, id);
    }

    @Override
    public List<GiftAndTagDto> findByTagName(String tagName) {
        String sql = "SELECT gc.*, t.*\n" +
                "FROM gift_certificate as gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.tag_id = gt.tag_id\n" +
                "WHERE gc.gift_id IN (SELECT gc.gift_id\n" +
                "                     FROM gift_certificate as gc\n" +
                "                              LEFT JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "                              LEFT JOIN tag t on t.tag_id = gt.tag_id\n" +
                "                     WHERE t.tag_name = ?)\n" +
                "ORDER BY gc.gift_id";
        return jdbcTemplate.query(sql, giftAndTagExtractor, tagName);

    }

    @Override
    public List<GiftAndTagDto> findByNameOrDescriptionContaining(String substr) {
        String sql = "SELECT gc.*, t.*\n" +
                "FROM gift_certificate as gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.tag_id = gt.tag_id\n" +
                "WHERE gc.gift_name LIKE CONCAT('%', ?, '%') OR gc.description LIKE CONCAT('%', ?, '%')" +
                "ORDER BY gc.gift_id";
        return jdbcTemplate.query(sql, giftAndTagExtractor, substr, substr);
    }

    @Override
    public List<GiftAndTagDto> findByAnyParams(String tagName, String substr) {
      /*  SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("tagName", tagName)
                .addValue("substr", substr);*/

        String sql = "SELECT gc.*, t.*\n" +
                "FROM gift_certificate as gc\n" +
                "         JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "         JOIN tag t on t.tag_id = gt.tag_id\n" +
                "WHERE gc.gift_id IN (SELECT gc.gift_id\n" +
                "                     FROM gift_certificate as gc\n" +
                "                              JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "                              JOIN tag t on t.tag_id = gt.tag_id\n" +
                "                     WHERE ((t.tag_name " + (tagName == null ? "IS NULL" : "= ?\n") +
                "AND gc.gift_name" + (substr == null ? "IS NULL" : "LIKE CONCAT('%', ?, '%') OR gc.description LIKE CONCAT('%', ?, '%'))\n") +
                "ORDER BY gc.gift_id";

        return jdbcTemplate.query(sql, giftAndTagExtractor, tagName, substr, substr);

    }

    @Override
    public Integer findId(GiftCertificate gc) {
        String sql = "SELECT gc.gift_id FROM gift_certificate gc\n" +
                "WHERE gc.gift_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, gc.getName());
    }

    @Override
    public int update(String key, Object value, Integer id) {
        String sql = String.format("UPDATE gift_certificate SET %s", key) + "= ?, last_update_date = ? WHERE gift_id = ?";
        return jdbcTemplate.update(sql, value, Timestamp.from(Instant.now()), id);
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM gift_certificate where gift_id = ?";
        return jdbcTemplate.update(sql, id);
    }


}
