package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.UsersOrder;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
        Integer userId = findIdOfUserWhoHaveTheHighestCostOfOrders();
        List<Integer> listOfGiftId = findGiftsIdByUserId(userId);

        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tag> cr = cb.createQuery(Tag.class);
        Root<Tag> root = cr.from(Tag.class);
        Join<Tag, GiftCertificate> join = root.join("gifts", JoinType.INNER);
        Expression<Long> exp = join.get("id");
        Predicate predicate = exp.in(listOfGiftId);
        CriteriaQuery<Tag> select = cr.select(root).where(predicate).groupBy(root.get("id"))
                .orderBy(cb.desc(cb.count(root.get("name"))));
        Query<Tag> query = session.createQuery(select);
        query.setFirstResult(0);
        query.setMaxResults(1);
        Tag tag = query.getSingleResult();
        session.close();
        return tag;
    }

    public Integer findIdOfUserWhoHaveTheHighestCostOfOrders() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
        Root<UsersOrder> root = cr.from(UsersOrder.class);
        CriteriaQuery<Integer> select = cr.select(root.get("userId")).groupBy(root.get("userId"))
                .orderBy(cb.desc(cb.sum(root.get("cost"))));
        Query<Integer> query = session.createQuery(select);
        query.setFirstResult(0);
        query.setMaxResults(1);
        Integer userId = query.getSingleResult();
        session.close();
        return userId;
    }

    public List<Integer> findGiftsIdByUserId(Integer userId) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
        Root<UsersOrder> root = cr.from(UsersOrder.class);
        CriteriaQuery<Integer> select = cr.select(root.get("giftId")).where(cb.equal(root.get("userId"), userId));
        Query<Integer> query = session.createQuery(select);
        List<Integer> listOfGiftId = query.getResultList();
        session.close();
        return listOfGiftId;
    }

}
