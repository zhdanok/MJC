package com.epam.esm.repository.impl;

import com.epam.esm.entity.UserProfile;
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
	public List<UserProfile> findAll(Integer skip, Integer limit) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<UserProfile> criteriaQuery = criteriaBuilder.createQuery(UserProfile.class);
		Root<UserProfile> root = criteriaQuery.from(UserProfile.class);
		criteriaQuery.select(root).orderBy(criteriaBuilder.asc(root.get("userId")));
		TypedQuery<UserProfile> typedQuery = session.createQuery(criteriaQuery);
		typedQuery.setFirstResult(skip);
		typedQuery.setMaxResults(limit);
		List<UserProfile> list = typedQuery.getResultList();
		session.close();
		return list;
	}

	@Override
	public UserProfile findById(Integer id) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<UserProfile> criteriaQuery = criteriaBuilder.createQuery(UserProfile.class);
		Root<UserProfile> root = criteriaQuery.from(UserProfile.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("userId"), id));
		UserProfile userProfile = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return userProfile;
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
		session.close();
		return usersOrder;
	}

	@Override
	public Long findSize() {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<UserProfile> root = criteriaQuery.from(UserProfile.class);
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
	public void saveUser(UserProfile userProfile) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(userProfile);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public Integer findUserIdByUserName(String name) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<UserProfile> root = criteriaQuery.from(UserProfile.class);
		criteriaQuery.select(root.get("userId")).where(criteriaBuilder.equal(root.get("userName"), name));
		Integer id = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

	@Override
	public UserProfile findByLogin(String login) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<UserProfile> criteriaQuery = criteriaBuilder.createQuery(UserProfile.class);
		Root<UserProfile> root = criteriaQuery.from(UserProfile.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("login"), login));
		UserProfile userProfile = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return userProfile;
	}

	@Override
	public Integer findIdByLogin(String login) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<UserProfile> root = criteriaQuery.from(UserProfile.class);
		criteriaQuery.select(root.get("userId")).where(criteriaBuilder.equal(root.get("login"), login));
		Integer id = session.createQuery(criteriaQuery).getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

}
