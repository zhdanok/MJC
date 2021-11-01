package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GiftCertificateDaoImpl implements GiftCertificateDao {

	private final SessionFactory sessionFactory;

	/**
	 * Send request for saving GiftCertificate
	 * @param gc - Entity which need to save
	 */
	@Override
	public void save(GiftCertificate gc) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.saveOrUpdate(gc);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Send request for getting Certificate by id
	 * @param id - Integer
	 * @return GiftCertificate with Tags
	 */
	@Override
	public GiftCertificate findById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<GiftCertificate> cr = cb.createQuery(GiftCertificate.class);
		Root<GiftCertificate> root = cr.from(GiftCertificate.class);
		root.fetch("tags", JoinType.LEFT);
		CriteriaQuery<GiftCertificate> select = cr.select(root).where(cb.equal(root.get("id"), id));
		org.hibernate.query.Query<GiftCertificate> query = session.createQuery(select);
		GiftCertificate gift = query.getResultStream().findFirst().orElse(null);
		session.close();
		return gift;
	}

	/**
	 * Send request for getting Certificates with Tags. There is possible to choose with
	 * any params or without it (return all Certificates with their Tags)
	 *
	 * @param size   - size of Array of Tag's name(optional, can be one or several)
	 * @param skip   - limip of Certificates which need to skip
	 * @param limit  - Limit of results at Page (optional)
	 * @param substr - String - substring that can be contained into name or description
	 *               (optional)
	 * @param sort   - String - style of sorting (optional): name-asc/name-desc - by Tag's
	 *               name asc/desc date-asc/date-desc - by Date of creation asc/desc
	 *               name-date-asc/name-date-desc - by Tag's name and then by Date of creating asc/desc
	 * @return List of GiftCertificate with Tags
	 */
	@Override
	public List<GiftCertificate> findByAnyParams(Long size, String substr, Integer skip, Integer limit, String sort) {
		Session session = sessionFactory.openSession();
		//This query cannot be written using Criteria API
		NativeQuery query = session.createNativeQuery("SELECT gc.* FROM (SELECT gc.*\n"
						+ "      FROM gift_certificate AS gc\n"
						+ "      WHERE ((:countOfTags IS NULL) OR gc.gift_id IN (SELECT gt.gift_id\n"
						+ "                                           FROM gifts_tags gt\n"
						+ "                                                   INNER JOIN tag t ON t.tag_id = gt.tag_id\n"
						+ "                                                   INNER JOIN searchtags s ON t.tag_name = s.stag_name\n"
						+ "                                           GROUP BY gt.gift_id\n"
						+ "                                           HAVING count(gift_id) = :countOfTags))\n"
						+ "        AND ((:substr IS NULL) OR\n"
						+ "             (gc.gift_name LIKE CONCAT('%', :substr, '%') OR gc.description LIKE CONCAT('%', :substr, '%')))\n"
						+ "     ) AS gc LEFT JOIN gifts_tags gt\n"
						+ "                   on gc.gift_id = gt.gift_id\n"
						+ "         LEFT JOIN tag t on t.tag_id = gt.tag_id GROUP BY gc.gift_id ORDER BY gc.gift_id",
				GiftCertificate.class);
		query.setParameter("countOfTags", size);
		query.setParameter("substr", substr);
		List<GiftCertificate> giftsWithoutTags = (List<GiftCertificate>) query.getResultList();
		List<GiftCertificate> finalList = sortBySortType(giftsWithoutTags, sort).stream().skip(skip).limit(limit)
				.collect(Collectors.toList());
		session.close();
		return finalList;
	}

	/**
	 * Send request for getting price of Certificate by id
	 * @param id - Integer
	 * @return Double
	 */
	@Override
	public Double findPriceById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Double> cr = cb.createQuery(Double.class);
		Root<GiftCertificate> root = cr.from(GiftCertificate.class);
		CriteriaQuery<Double> select = cr.select(root.get("price")).where(cb.equal(root.get("id"), id));
		org.hibernate.query.Query<Double> query = session.createQuery(select);
		Double price = query.getResultStream().findFirst().orElse(null);
		session.close();
		return price;
	}

	/**
	 * Send request for getting name of Certificate by id
	 * @param id - Integer
	 * @return String name
	 */
	@Override
	public String findNameById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<String> cr = cb.createQuery(String.class);
		Root<GiftCertificate> root = cr.from(GiftCertificate.class);
		CriteriaQuery<String> select = cr.select(root.get("name")).where(cb.equal(root.get("id"), id));
		org.hibernate.query.Query<String> query = session.createQuery(select);
		String name = query.getResultStream().findFirst().orElse(null);
		session.close();
		return name;
	}

	/**
	 * Send request for updating only fields in GiftCertificate
	 * @param id - Integer id
	 * @param updates - Map<String, Object>, String - name of field, Object - value of
	 * field
	 */
	@Override
	public int update(Map<String, Object> updates, Integer id, Instant now) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaUpdate<GiftCertificate> criteriaUpdate = cb.createCriteriaUpdate(GiftCertificate.class);
		Root<GiftCertificate> root = criteriaUpdate.from(GiftCertificate.class);
		updates.forEach((key, value) -> {
			criteriaUpdate.set(key, value);
		});
		criteriaUpdate.set("lastUpdateDate", now);
		criteriaUpdate.where(cb.equal(root.get("id"), id));
		session.beginTransaction();
		int size = session.createQuery(criteriaUpdate).executeUpdate();
		session.getTransaction().commit();
		session.close();
		return size;
	}

	/**
	 * Send request for deleting GiftCertificate
	 * @param id - Integer id
	 */
	@Override
	public int deleteById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaDelete<GiftCertificate> criteriaDelete = cb.createCriteriaDelete(GiftCertificate.class);
		Root<GiftCertificate> root = criteriaDelete.from(GiftCertificate.class);
		criteriaDelete.where(cb.equal(root.get("id"), id));
		session.beginTransaction();
		int size = session.createQuery(criteriaDelete).executeUpdate();
		session.getTransaction().commit();
		session.close();
		return size;
	}

	/**
	 * Return count of Results which match the search parameters
	 * @param countOfTags - size of Array of Tag's name(optional, can be one or several)
	 * @param substr - String - substring that can be contained into name or description
	 * (optional)
	 * @return total - count of Results which match the search parameters
	 */
	@Override
	public Long findSize(Long countOfTags, String substr) {
		Session session = sessionFactory.openSession();
		//This query cannot be written using Criteria API
		NativeQuery query = session.createNativeQuery("SELECT COUNT(*) as size\n"
				+ "      FROM gift_certificate AS gc\n"
				+ "      WHERE ((:countOfTags IS NULL) OR gc.gift_id IN (SELECT gt.gift_id\n"
				+ "                                           FROM gifts_tags gt\n"
				+ "                                                   INNER JOIN tag t ON t.tag_id = gt.tag_id\n"
				+ "                                                   INNER JOIN searchtags s ON t.tag_name = s.stag_name\n"
				+ "                                           GROUP BY gt.gift_id\n"
				+ "                                           HAVING count(gift_id) = :countOfTags))\n"
				+ "        AND ((:substr IS NULL) OR\n"
				+ "             (gc.gift_name LIKE CONCAT('%', :substr, '%') OR gc.description LIKE CONCAT('%', :substr, '%')))");
		query.setParameter("countOfTags", countOfTags);
		query.setParameter("substr", substr);
		Long total = ((BigInteger) query.getSingleResult()).longValue();
		session.close();
		return total;
	}

	/**
	 * Send request for getting id of Certificate by name
	 * @param name - String name of GiftCertificate
	 * @return Integer - id of GiftCertificate
	 */
	@Override
	public Integer findGiftIdByGiftName(String name) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
		Root<GiftCertificate> root = cr.from(GiftCertificate.class);
		CriteriaQuery<Integer> select = cr.select(root.get("id")).where(cb.equal(root.get("name"), name));
		Query<Integer> query = session.createQuery(select);
		Integer id = query.getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

	private List<GiftCertificate> sortBySortType(List<GiftCertificate> list, String sort) {
		if (sort == null) {
			return list;
		} else {
			switch (sort) {
				case "name-asc":
					return list.stream().sorted(Comparator.comparing(GiftCertificate::getName))
							.collect(Collectors.toList());
				case "name-desc":
					return list.stream().sorted(Comparator.comparing(GiftCertificate::getName).reversed())
							.collect(Collectors.toList());
				case "date-asc":
					return list.stream().sorted(Comparator.comparing(GiftCertificate::getCreateDate))
							.collect(Collectors.toList());
				case "date-desc":
					return list.stream().sorted(Comparator.comparing(GiftCertificate::getCreateDate).reversed())
							.collect(Collectors.toList());
				case "name-date-asc":
					return list.stream().sorted(
									Comparator.comparing(GiftCertificate::getName).thenComparing(GiftCertificate::getCreateDate))
							.collect(Collectors.toList());
				case "name-date-desc":
					return list.stream().sorted(Comparator.comparing(GiftCertificate::getName)
							.thenComparing(GiftCertificate::getCreateDate).reversed()).collect(Collectors.toList());
			default:
				return list;
			}
		}
	}

}
