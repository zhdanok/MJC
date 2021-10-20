package com.epam.esm.repository;

import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.mapper.GiftAndTagExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    private final GiftAndTagExtractor giftAndTagExtractor;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(GiftCertificate gc) {
        String sql = "INSERT INTO gift_certificate(gift_name, description, price, duration, create_date, " +
                "last_update_date)  VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, gc.getName(), gc.getDescription(),
                gc.getPrice(), gc.getDuration(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));
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
    public List<GiftAndTagDto> findByAnyParams(Integer size, String substr) {
        String sqlSetSize = "SET @size = ?";
        String sqlSetSubstr = "SET @substr = ?";

        jdbcTemplate.update(sqlSetSize, size);
        jdbcTemplate.update(sqlSetSubstr, substr);

        String sql = "SELECT gc.*, t.*\n" +
                "FROM gift_certificate as gc\n" +
                "         LEFT JOIN gifts_tags gt on gc.gift_id = gt.gift_id\n" +
                "         LEFT JOIN tag t on t.tag_id = gt.tag_id\n" +
                "WHERE ((@size IS NULL) OR gc.gift_id IN (select gt.gift_id\n" +
                "                      from gifts_tags gt\n" +
                "                               join tag t on t.tag_id = gt.tag_id\n" +
                "                               join searchtags s on t.tag_name = s.stag_name\n" +
                "                      group by gt.gift_id\n" +
                "                      having count(gift_id) = @size)\n" +
                "    AND ((@substr IS NULL) OR\n" +
                "         (gc.gift_name LIKE CONCAT('%', @substr, '%') OR gc.description LIKE CONCAT('%', @substr, '%'))))\n" +
                "ORDER BY gc.gift_id";
        return jdbcTemplate.query(sql, giftAndTagExtractor);
    }

    @Override
    public Integer findId(GiftCertificate gc) {
        String sql = "SELECT gc.gift_id FROM gift_certificate gc\n" +
                "WHERE gc.gift_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, gc.getName());
    }

    @Override
    public Double findPriceById(Integer id) {
        String sql = "SELECT gc.price FROM gift_certificate gc\n" +
                "WHERE gc.gift_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, id);
    }

    @Override
    public String findNameById(Integer id) {
        String sql = "SELECT gc.gift_name FROM gift_certificate gc\n" +
                "WHERE gc.gift_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, id);
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
