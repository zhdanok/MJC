package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
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

	@Override
	public List<GiftCertificate> findByAnyParams(Long size, String substr, Integer skip, Integer limit, String sort) {
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
		List<GiftCertificate> giftsWithoutTags = (List<GiftCertificate>) query.getResultList();
		List<GiftCertificate> finalList = sortBySortType(giftsWithoutTags, sort).stream().skip(skip).limit(limit)
				.collect(Collectors.toList());
		session.close();
		return finalList;
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
	public Long findSize(Long countOfTags, String substr) {
		Session session = sessionFactory.openSession();
		/*
		 * session.beginTransaction(); List<Integer> giftsId =
		 * findGiftsIdWhichContainedRequirementTags(countOfTags);
		 *
		 * CriteriaBuilder cb = session.getCriteriaBuilder(); CriteriaQuery<Long> cr =
		 * cb.createQuery(Long.class); Root<GiftCertificate> root =
		 * cr.from(GiftCertificate.class); root.fetch("tags", JoinType.LEFT); Predicate
		 * giftIdIn = null; Predicate likeNameOrDescription = null; Predicate
		 * finalPredicate = null;
		 *
		 * if(countOfTags != null) { Expression<Integer> giftId = root.get("id"); giftIdIn
		 * = giftId.in(giftsId); }
		 *
		 * if (substr != null) { Expression<String> name = root.get("name");
		 * Expression<String> description = root.get("description"); Predicate likeName =
		 * cb.like(name, '%' + substr + '%'); Predicate likeDescription =
		 * cb.like(description, '%' + substr + '%'); likeNameOrDescription =
		 * cb.or(likeName, likeDescription); }
		 *
		 * if(giftIdIn != null && likeNameOrDescription != null) { finalPredicate =
		 * cb.and(giftIdIn, likeNameOrDescription); } else if (giftIdIn == null) {
		 * finalPredicate = likeNameOrDescription; } else if (likeNameOrDescription ==
		 * null) { finalPredicate = giftIdIn; } Long total = null; if (finalPredicate ==
		 * null) { CriteriaQuery<Long> select = cr.select(cb.count(root)); Query<Long>
		 * query = session.createQuery(select); total = query.getSingleResult(); } else {
		 * CriteriaQuery<Long> select = cr.select(cb.count(root)) .where(finalPredicate);
		 * Query<Long> query = session.createQuery(select); total =
		 * query.getSingleResult(); }
		 *
		 * session.getTransaction().commit();
		 */
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

	/*
	 * public List<Integer> findGiftsIdWhichContainedRequirementTags(Long countOfTags) {
	 * Session session = sessionFactory.openSession(); Query query =
	 * session.createQuery("select gt.id from GiftsTags gt " +
	 * "inner join Tag t on t.id = gt.tagId " +
	 * "inner join SearchTags s on t.name = s.name " + "group by gt.id " +
	 * "having count (gt.id) = :countOfTags"); query.setParameter("countOfTags",
	 * countOfTags); List<Integer> giftsId = (List<Integer>) query.getResultList();
	 * session.close(); return giftsId; }
	 */

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
