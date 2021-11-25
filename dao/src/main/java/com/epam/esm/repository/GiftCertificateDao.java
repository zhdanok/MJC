package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface GiftCertificateDao {

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
	 * Return count of Results which match the search parameters
	 *
	 * @param size   - size of Array of Tag's name(optional, can be one or several)
	 * @param substr - String - substring that can be contained into name or description
	 *               (optional)
	 * @return total - count of Results which match the search parameters
	 */
	Long findSize(Long size, String substr);

}
