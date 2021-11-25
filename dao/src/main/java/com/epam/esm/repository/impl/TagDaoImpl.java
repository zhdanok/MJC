package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.UsersOrder;
import com.epam.esm.repository.TagDao;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagDaoImpl implements TagDao {

    private final SessionFactory sessionFactory;

    @Override
    public Tag findMostPopularTagOfUserWithHighestCostOfOrder() {
        Integer userId = findIdOfUserWhoHaveTheHighestCostOfOrders();
        List<Integer> listOfGiftId = findGiftsIdByUserId(userId);

        Session session = sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        Join<Tag, GiftCertificate> join = root.join("gifts", JoinType.INNER);
        Expression<Long> exp = join.get("id");
        Predicate predicate = exp.in(listOfGiftId);
        criteriaQuery.select(root).where(predicate).groupBy(root.get("id"))
                .orderBy(criteriaBuilder.desc(criteriaBuilder.count(root.get("name"))));
        Query<Tag> query = session.createQuery(criteriaQuery);
        query.setFirstResult(0);
        query.setMaxResults(1);
        Tag tag = query.getSingleResult();
        session.close();
        return tag;
    }

    /**
     * Send request for getting id of user who have the highest cost of orders
     *
     * @return Id of User
     */
    public Integer findIdOfUserWhoHaveTheHighestCostOfOrders() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
        Root<UsersOrder> root = criteriaQuery.from(UsersOrder.class);
        criteriaQuery.select(root.get("userId")).groupBy(root.get("userId"))
                .orderBy(criteriaBuilder.desc(criteriaBuilder.sum(root.get("cost"))));
        Query<Integer> query = session.createQuery(criteriaQuery);
        query.setFirstResult(0);
        query.setMaxResults(1);
        Integer userId = query.getSingleResult();
        session.close();
        return userId;
    }

    /**
     * Send request for getting List of GiftCertificate's id of user by User's id
     *
     * @return List of GiftCertificate's id
     */
    public List<Integer> findGiftsIdByUserId(Integer userId) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
        Root<UsersOrder> root = criteriaQuery.from(UsersOrder.class);
        criteriaQuery.select(root.get("giftId")).where(criteriaBuilder.equal(root.get("userId"), userId));
        List<Integer> listOfGiftId = session.createQuery(criteriaQuery).getResultList();
        session.close();
        return listOfGiftId;
    }
}
