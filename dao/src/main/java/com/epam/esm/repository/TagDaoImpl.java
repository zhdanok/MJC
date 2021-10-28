package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagDaoImpl implements TagDao {

	private final SessionFactory sessionFactory;

	@Override
	public void save(Tag tag) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(tag);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<Tag> findAll(Integer skip, Integer limit) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Tag> cr = cb.createQuery(Tag.class);
		Root<Tag> root = cr.from(Tag.class);
		CriteriaQuery<Tag> select = cr.select(root).orderBy(cb.asc(root.get("id")));
		TypedQuery<Tag> typedQuery = session.createQuery(select);
		typedQuery.setFirstResult(skip);
		typedQuery.setMaxResults(limit);
		List<Tag> list = typedQuery.getResultList();
		session.close();
		return list;
	}

	@Override
	public Tag findById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Tag> cr = cb.createQuery(Tag.class);
		Root<Tag> root = cr.from(Tag.class);
		CriteriaQuery<Tag> select = cr.select(root).where(cb.equal(root.get("id"), id));
		Query<Tag> query = session.createQuery(select);
		Tag tag = query.getResultStream().findFirst().orElse(null);
		session.close();
		return tag;
	}

	@Override
	public Integer findTagIdByTagName(String name) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
		Root<Tag> root = cr.from(Tag.class);
		CriteriaQuery<Integer> select = cr.select(root.get("id")).where(cb.equal(root.get("name"), name));
		Query<Integer> query = session.createQuery(select);
		Integer id = query.getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

	@Override
	public int deleteById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaDelete<Tag> criteriaDelete = cb.createCriteriaDelete(Tag.class);
		Root<Tag> root = criteriaDelete.from(Tag.class);
		criteriaDelete.where(cb.equal(root.get("id"), id));
		session.beginTransaction();
		int size = session.createQuery(criteriaDelete).executeUpdate();
		session.getTransaction().commit();
		session.close();
		return size;
	}

	@Override
	public Long findSize() {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<Tag> root = cr.from(Tag.class);
		CriteriaQuery<Long> select = cr.select(cb.count(root.get("id")));
		Query<Long> query = session.createQuery(select);
		Long total = query.getSingleResult();
		session.close();
		return total;
	}

	@Override
	public Tag findMostPopularTagOfUserWithHighestCostOfOrder() {
		Session session = sessionFactory.openSession();
		Query query = session
				.createNativeQuery("SELECT t.* from tag t\n" + "JOIN gifts_tags gt on t.tag_id = gt.tag_id\n"
						+ "JOIN gift_certificate gc on gc.gift_id = gt.gift_id\n" + "WHERE gc.gift_id IN (\n"
						+ "SELECT o.gift_id from users_order o where o.user_id = ("
						+ "SELECT o.user_id from users_order o group by o.user_id order by SUM(o.cost) DESC limit 1))\n"
						+ "group by t.tag_id order by COUNT(t.tag_name) DESC limit 1", Tag.class);
		Tag tag = (Tag) query.getSingleResult();
		session.close();
		return tag;
	}

}
