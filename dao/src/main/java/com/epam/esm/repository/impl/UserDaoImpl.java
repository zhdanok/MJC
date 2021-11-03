package com.epam.esm.repository.impl;

import com.epam.esm.entity.User;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.select(root).orderBy(criteriaBuilder.asc(root.get("userId")));
		TypedQuery<User> typedQuery = session.createQuery(criteriaQuery);
		typedQuery.setFirstResult(skip);
		typedQuery.setMaxResults(limit);
		List<User> list = typedQuery.getResultList();
		session.close();
		return list;
	}

	@Override
	public User findById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("userId"), id));
		User user = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
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
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<UsersOrder> criteriaQuery = criteriaBuilder.createQuery(UsersOrder.class);
		Root<UsersOrder> root = criteriaQuery.from(UsersOrder.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("userId"), id));
		TypedQuery<UsersOrder> typedQuery = session.createQuery(criteriaQuery);
		typedQuery.setFirstResult(skip);
		typedQuery.setMaxResults(limit);
		List<UsersOrder> list = typedQuery.getResultList();
		session.close();
		return list;
	}

	@Override
	public UsersOrder findCostAndDateOfBuyForUserByOrderId(Integer userId, Integer orderId) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<UsersOrder> criteriaQuery = criteriaBuilder.createQuery(UsersOrder.class);
		Root<UsersOrder> root = criteriaQuery.from(UsersOrder.class);
		criteriaQuery.multiselect(root.get("cost"), root.get("dateOfBuy"));
		Predicate predicateForUserId = criteriaBuilder.equal(root.get("userId"), userId);
		Predicate predicateForOrderId = criteriaBuilder.equal(root.get("orderId"), orderId);
		Predicate finalPredicate = criteriaBuilder.and(predicateForUserId, predicateForOrderId);
		criteriaQuery.where(finalPredicate);
		UsersOrder usersOrder = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		return usersOrder;
	}

	@Override
	public Long findSize() {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.select(criteriaBuilder.count(root.get("userId")));
		Long total = session.createQuery(criteriaQuery).getSingleResult();
		session.close();
		return total;
	}

	@Override
	public Long findUsersOrdersSize(Integer userId) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<UsersOrder> root = criteriaQuery.from(UsersOrder.class);
		criteriaQuery.select(criteriaBuilder.count(root.get("orderId")))
				.where(criteriaBuilder.equal(root.get("userId"), userId));
		Long total = session.createQuery(criteriaQuery).getSingleResult();
		session.close();
		return total;
	}

	@Override
	public void saveUser(User user) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public Integer findUserIdByUserName(String name) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.select(root.get("userId")).where(criteriaBuilder.equal(root.get("userName"), name));
		Integer id = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

}
