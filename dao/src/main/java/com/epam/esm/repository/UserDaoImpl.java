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

	/**
	 * Send request for getting all Users with required page and limit
	 *
	 * @param skip  - count of page which need to skip
	 * @param limit - count of Users which need to view at page
	 * @return List of User with requirement parameters
	 */
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

	/**
	 * Send request for getting User by id
	 *
	 * @param id - Integer
	 * @return Instance of User
	 */
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

	/**
	 * Send request for saving Order
	 */
	@Override
	public void save(UsersOrder usersOrder) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(usersOrder);
		session.getTransaction().commit();
		session.close();

	}

	/**
	 * Send request for getting User's orders
	 *
	 * @param id    - Integer - User's id
	 * @param skip  - count of page which need to skip
	 * @param limit - count of Orders which need to view at page
	 * @return List of UsersOrder
	 */
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

	/**
	 * Send request for getting UsersOrder for User by User id and Order id
	 *
	 * @param userId  - Integer - User's id
	 * @param orderId - Integer - Order's id
	 * @return UsersOrder
	 */
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

	/**
	 * Send request for getting count of all Users
	 *
	 * @return Long
	 */
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

	/**
	 * Send request for getting count of all Orders for User by User's id
	 *
	 * @param userId - Integer - User's id
	 * @return Long
	 */
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

	/**
	 * Send request for saving User
	 *
	 * @param user - Entity which need to save
	 */
	@Override
	public void saveUser(User user) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Send request for getting User's id by User's name
	 *
	 * @param name - User's name
	 * @return User's id
	 */
	@Override
	public Integer findUserIdByUserName(String name) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Integer> cr = cb.createQuery(Integer.class);
		Root<User> root = cr.from(User.class);
		CriteriaQuery<Integer> select = cr.select(root.get("userId")).where(cb.equal(root.get("userName"), name));
		Query<Integer> query = session.createQuery(select);
		Integer id = query.getResultStream().findFirst().orElse(null);
		session.close();
		return id;
	}

}
