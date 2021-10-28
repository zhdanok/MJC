package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class GiftCertificateDaoImpl implements GiftCertificateDao {

	private final SessionFactory sessionFactory;

	@Override
	public void save(GiftCertificate gc) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.saveOrUpdate(gc);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public GiftCertificate findById(Integer id) {
		Session session = sessionFactory.openSession();
		Query query = session
				.createQuery("select gc from GiftCertificate gc " + "left join fetch gc.tags t " + "where gc.id = :id");
		query.setParameter("id", id);
		GiftCertificate gift = (GiftCertificate) query.getResultStream().findFirst().orElse(null);
		session.close();
		return gift;
	}

	@Override
	public List<GiftCertificate> findByAnyParams(Long size, String substr, Integer skip, Integer limit) {
		Session session = sessionFactory.openSession();
		NativeQuery query = session.createNativeQuery("SELECT gc.*\n" + "FROM (SELECT gc.*\n"
						+ "      FROM gift_certificate AS gc\n"
						+ "      WHERE ((:countOfTags IS NULL) OR gc.gift_id IN (SELECT gt.gift_id\n"
						+ "                                           FROM gifts_tags gt\n"
						+ "                                                   INNER JOIN tag t ON t.tag_id = gt.tag_id\n"
						+ "                                                   INNER JOIN searchtags s ON t.tag_name = s.stag_name\n"
						+ "                                           GROUP BY gt.gift_id\n"
						+ "                                           HAVING count(gift_id) = :countOfTags))\n"
						+ "        AND ((:substr IS NULL) OR\n"
						+ "             (gc.gift_name LIKE CONCAT('%', :substr, '%') OR gc.description LIKE CONCAT('%', :substr, '%')))\n"
						+ "     ) AS gc\n" + "         LEFT JOIN gifts_tags gt\n"
						+ "                   on gc.gift_id = gt.gift_id\n"
						+ "         LEFT JOIN tag t on t.tag_id = gt.tag_id\n" + "GROUP BY gc.gift_id ORDER BY gc.gift_id",
				GiftCertificate.class);
		query.setParameter("countOfTags", size);
		query.setParameter("substr", substr);
		query.setFirstResult(skip);
		query.setMaxResults(limit);

		List<GiftCertificate> giftsWithoutTags = (List<GiftCertificate>) query.getResultList();

		session.close();
		return giftsWithoutTags;
	}

	@Override
	public Integer findId(GiftCertificate gc) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
		Root<GiftCertificate> root = cr.from(GiftCertificate.class);
		CriteriaQuery<Integer> select = cr.select(root.get("id")).where(cb.equal(root.get("name"), gc.getName()));
		org.hibernate.query.Query<Integer> query = session.createQuery(select);
		Integer id = query.getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

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

	@Override
	public Long findSize(Long size, String substr) {
		Session session = sessionFactory.openSession();
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
		query.setParameter("countOfTags", size);
		query.setParameter("substr", substr);
		Long total = ((BigInteger) query.getSingleResult()).longValue();
		session.close();
		return total;
	}

}
