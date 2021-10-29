package com.epam.esm.repository;

import com.epam.esm.entity.User;
import com.epam.esm.entity.UsersOrder;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

	private final SessionFactory sessionFactory;

	@Override
	public List<User> findAll(Integer skip, Integer limit) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> cr = cb.createQuery(User.class);
		Root<User> root = cr.from(User.class);
		CriteriaQuery<User> select = cr.select(root).orderBy(cb.asc(root.get("userId")));
		TypedQuery<User> typedQuery = session.createQuery(select);
		typedQuery.setFirstResult(skip);
		typedQuery.setMaxResults(limit);
		List<User> list = typedQuery.getResultList();
		session.close();
		return list;
	}

	@Override
	public User findById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> cr = cb.createQuery(User.class);
		Root<User> root = cr.from(User.class);
		CriteriaQuery<User> select = cr.select(root).where(cb.equal(root.get("userId"), id));
		Query<User> query = session.createQuery(select);
		User user = query.getResultStream().findFirst().orElse(null);
		session.close();
		return user;
	}

	@Override
	public void save(UsersOrder usersOrder) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(usersOrder);
		session.getTransaction().commit();
		session.close();

	}

	@Override
	public List<UsersOrder> findOrdersByUserId(Integer id, Integer skip, Integer limit) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<UsersOrder> cr = cb.createQuery(UsersOrder.class);
		Root<UsersOrder> root = cr.from(UsersOrder.class);
		CriteriaQuery<UsersOrder> select = cr.select(root).where(cb.equal(root.get("userId"), id));
		TypedQuery<UsersOrder> typedQuery = session.createQuery(select);
		typedQuery.setFirstResult(skip);
		typedQuery.setMaxResults(limit);
		List<UsersOrder> list = typedQuery.getResultList();
		session.close();
		return list;
	}

	@Override
	public UsersOrder findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<UsersOrder> cq = cb.createQuery(UsersOrder.class);
		Root<UsersOrder> root = cq.from(UsersOrder.class);
		cq.multiselect(root.get("cost"), root.get("dateOfBuy"));
		Predicate predicateForUserId = cb.equal(root.get("userId"), userId);
		Predicate predicateForOrderId = cb.equal(root.get("orderId"), orderId);
		Predicate finalPredicate = cb.and(predicateForUserId, predicateForOrderId);
		cq.where(finalPredicate);
		UsersOrder usersOrder = session.createQuery(cq).getResultStream().findFirst().orElse(null);
		return usersOrder;
	}

	@Override
	public Long findSize() {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<User> root = cr.from(User.class);
		CriteriaQuery<Long> select = cr.select(cb.count(root.get("userId")));
		Query<Long> query = session.createQuery(select);
		Long total = query.getSingleResult();
		session.close();
		return total;
	}

	@Override
	public Long findUsersOrdersSize(Integer userId) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Long> cr = cb.createQuery(Long.class);
		Root<UsersOrder> root = cr.from(UsersOrder.class);
		CriteriaQuery<Long> select = cr.select(cb.count(root.get("orderId")))
				.where(cb.equal(root.get("userId"), userId));
		Query<Long> query = session.createQuery(select);
		Long total = query.getSingleResult();
		session.close();
		return total;
	}

}
