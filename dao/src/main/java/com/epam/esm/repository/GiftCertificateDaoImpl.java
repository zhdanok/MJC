package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftsTags;
import com.epam.esm.entity.SearchTags;
import com.epam.esm.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<GiftCertificate> criteriaQuery = criteriaBuilder.createQuery(GiftCertificate.class);
		Root<GiftCertificate> root = criteriaQuery.from(GiftCertificate.class);
		root.fetch("tags", JoinType.LEFT);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		GiftCertificate gift = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return gift;
	}

	@Override
	public List<GiftCertificate> findByAnyParams(Long size, String substr, Integer skip, Integer limit, String sort,
												 String order) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<GiftCertificate> criteriaQuery2 = criteriaBuilder.createQuery(GiftCertificate.class);
		Root<GiftCertificate> giftCertificateRoot = criteriaQuery2.from(GiftCertificate.class);
		Predicate finalPredicate = getFinalPredicate(size, substr, session, criteriaBuilder, giftCertificateRoot);
		if (finalPredicate != null) {
			criteriaQuery2.select(giftCertificateRoot).where(finalPredicate).groupBy(giftCertificateRoot.get("id"));
		}
		orderByFieldName(sort, order, criteriaBuilder, criteriaQuery2, giftCertificateRoot);
		List<GiftCertificate> finalList = getPagingList(skip, limit, session, criteriaQuery2);
		session.close();
		return finalList;
	}

	@Override
	public Double findPriceById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Double> criteriaQuery = criteriaBuilder.createQuery(Double.class);
		Root<GiftCertificate> root = criteriaQuery.from(GiftCertificate.class);
		criteriaQuery.select(root.get("price")).where(criteriaBuilder.equal(root.get("id"), id));
		Double price = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return price;
	}

	@Override
	public String findNameById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
		Root<GiftCertificate> root = criteriaQuery.from(GiftCertificate.class);
		criteriaQuery.select(root.get("name")).where(criteriaBuilder.equal(root.get("id"), id));
		String name = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return name;
	}

	@Override
	public int update(Map<String, Object> updates, Integer id, Instant now) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaUpdate<GiftCertificate> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(GiftCertificate.class);
		Root<GiftCertificate> root = criteriaUpdate.from(GiftCertificate.class);
		updates.forEach((key, value) -> {
			criteriaUpdate.set(key, value);
		});
		criteriaUpdate.set("lastUpdateDate", now);
		criteriaUpdate.where(criteriaBuilder.equal(root.get("id"), id));
		session.beginTransaction();
		int size = session.createQuery(criteriaUpdate).executeUpdate();
		session.getTransaction().commit();
		session.close();
		return size;
	}

	@Override
	public int deleteById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaDelete<GiftCertificate> criteriaDelete = criteriaBuilder.createCriteriaDelete(GiftCertificate.class);
		Root<GiftCertificate> root = criteriaDelete.from(GiftCertificate.class);
		criteriaDelete.where(criteriaBuilder.equal(root.get("id"), id));
		session.beginTransaction();
		int size = session.createQuery(criteriaDelete).executeUpdate();
		session.getTransaction().commit();
		session.close();
		return size;
	}

	@Override
	public Long findSize(Long size, String substr) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery2 = criteriaBuilder.createQuery(Long.class);
		Root<GiftCertificate> giftCertificateRoot = criteriaQuery2.from(GiftCertificate.class);
		criteriaQuery2.select(criteriaBuilder.count(giftCertificateRoot.get("id")));
		Predicate finalPredicate = getFinalPredicate(size, substr, session, criteriaBuilder, giftCertificateRoot);
		if (finalPredicate != null) {
			criteriaQuery2.where(finalPredicate);
		}
		Long total = session.createQuery(criteriaQuery2).getSingleResult();
		session.close();
		return total;
	}

	@Override
	public Integer findGiftIdByGiftName(String name) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<GiftCertificate> root = criteriaQuery.from(GiftCertificate.class);
		criteriaQuery.select(root.get("id")).where(criteriaBuilder.equal(root.get("name"), name));
		Integer id = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

	private Predicate getFinalPredicate(Long size, String substr, Session session, CriteriaBuilder criteriaBuilder,
										Root<GiftCertificate> giftCertificateRoot) {
		Predicate finalPredicate = null;
		Predicate giftIdIn = getGiftIdInPredicate(size, session, criteriaBuilder, giftCertificateRoot);
		Predicate likeNameOrDescription = getLikeNameOrDescriptionPredicate(substr, criteriaBuilder,
				giftCertificateRoot);
		if (giftIdIn != null && likeNameOrDescription != null) {
			finalPredicate = criteriaBuilder.and(giftIdIn, likeNameOrDescription);
		} else if (giftIdIn == null && likeNameOrDescription != null) {
			finalPredicate = likeNameOrDescription;
		} else if (giftIdIn != null && likeNameOrDescription == null) {
			finalPredicate = giftIdIn;
		}
		return finalPredicate;
	}

	private Predicate getLikeNameOrDescriptionPredicate(String substr, CriteriaBuilder criteriaBuilder,
														Root<GiftCertificate> giftCertificateRoot) {
		Predicate likeNameOrDescription = null;
		if (substr != null) {
			Expression<String> name = giftCertificateRoot.get("name");
			Expression<String> description = giftCertificateRoot.get("description");
			Predicate likeName = criteriaBuilder.like(name, '%' + substr + '%');
			Predicate likeDescription = criteriaBuilder.like(description, '%' + substr + '%');
			likeNameOrDescription = criteriaBuilder.or(likeName, likeDescription);
		}
		return likeNameOrDescription;
	}

	private Predicate getGiftIdInPredicate(Long size, Session session, CriteriaBuilder criteriaBuilder,
										   Root<GiftCertificate> giftCertificateRoot) {
		Predicate giftIdIn = null;
		if (size != null) {
			// Find tag_id
			CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
			Root<Tag> tagRoot = criteriaQuery.from(Tag.class);
			Root<SearchTags> searchTagsRoot = criteriaQuery.from(SearchTags.class);
			criteriaQuery.select(tagRoot.get("id"))
					.where(criteriaBuilder.equal(tagRoot.get("name"), searchTagsRoot.get("name")));
			List<Integer> tagIdList = session.createQuery(criteriaQuery).getResultList();

			// Find gift_id
			CriteriaQuery<Integer> criteriaQuery1 = criteriaBuilder.createQuery(Integer.class);
			Root<GiftsTags> root = criteriaQuery1.from(GiftsTags.class);
			Predicate where = root.get("tagId").in(tagIdList);
			Predicate having = criteriaBuilder.equal(criteriaBuilder.count(root.get("giftId")), size);
			criteriaQuery1.select(root.get("giftId")).where(where).groupBy(root.get("giftId")).having(having);
			List<Integer> giftIdList = session.createQuery(criteriaQuery1).getResultList();

			// Predicate giftIdIn
			Expression<Integer> giftId = giftCertificateRoot.get("id");
			giftIdIn = giftId.in(giftIdList);
		}
		return giftIdIn;
	}

	private List<GiftCertificate> getPagingList(Integer skip, Integer limit, Session session,
												CriteriaQuery<GiftCertificate> criteriaQuery2) {
		TypedQuery<GiftCertificate> typedQuery = session.createQuery(criteriaQuery2);
		typedQuery.setFirstResult(skip);
		typedQuery.setMaxResults(limit);
		List<GiftCertificate> finalList = typedQuery.getResultList();
		return finalList;
	}

	private void orderByFieldName(String sort, String order, CriteriaBuilder criteriaBuilder,
								  CriteriaQuery<GiftCertificate> criteriaQuery2, Root<GiftCertificate> giftCertificateRoot) {
		Expression<Object> sortType = giftCertificateRoot.get(sort);
		if (order.equals("asc")) {
			criteriaQuery2.orderBy(criteriaBuilder.asc(sortType));
		} else if (order.equals("desc")) {
			criteriaQuery2.orderBy(criteriaBuilder.desc(sortType));
		}
	}

}
