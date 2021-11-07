package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface GiftCertificateDao {

	/**
	 * Send request for saving {@link GiftCertificate}
	 *
	 * @param giftCertificate - Entity which need to save
	 */
	void save(GiftCertificate giftCertificate);

	/**
	 * Send request for getting Certificate by id
	 *
	 * @param id - Integer
	 * @return GiftCertificate with Tags
	 */
	GiftCertificate findById(Integer id);

	/**
	 * Send request for getting {@link GiftCertificate} with {@link com.epam.esm.entity.Tag}. There is possible to choose with
	 * any params or without it (return all Certificates with their Tags)
	 *
	 * @param size   - size of Array of Tag's name(optional, can be one or several)
	 * @param substr - String - substring that can be contained into name or description
	 *               (optional)
	 * @param skip   - limip of Certificates which need to skip
	 * @param limit  - Limit of results at Page (optional)
	 * @param sort   - String - field of sorting (optional)
	 * @return List of GiftCertificate with Tags
	 */
	List<GiftCertificate> findByAnyParams(Long size, String substr, Integer skip, Integer limit, String[] sort);

	/**
	 * Send request for updating only fields in {@link GiftCertificate}
	 *
	 * @param id      - Integer id
	 * @param updates - Map<String, Object>, String - name of field, Object - value of
	 *                field
	 */
	int update(Map<String, Object> updates, Integer id, Instant now);

	/**
	 * Send request for deleting {@link GiftCertificate}
	 *
	 * @param id - Integer id
	 */
	int deleteById(Integer id);

	/**
	 * Send request for getting price of {@link GiftCertificate} by id
	 *
	 * @param id - Integer
	 * @return Double
	 */
	Double findPriceById(Integer id);

	/**
	 * Send request for getting name of {@link GiftCertificate} by id
	 *
	 * @param giftId - Integer
	 * @return String name
	 */
	String findNameById(Integer giftId);

	/**
	 * Return count of Results which match the search parameters
	 *
	 * @param size   - size of Array of Tag's name(optional, can be one or several)
	 * @param substr - String - substring that can be contained into name or description
	 *               (optional)
	 * @return total - count of Results which match the search parameters
	 */
	Long findSize(Long size, String substr);

	/**
	 * Send request for getting id of {@link GiftCertificate} by name
	 *
	 * @param name - String name of GiftCertificate
	 * @return Integer - id of GiftCertificate
	 */
	Integer findGiftIdByGiftName(String name);

}
