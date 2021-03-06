package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftsTags;
import com.epam.esm.entity.SearchTags;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.GiftCertificateDao;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    private final SessionFactory sessionFactory;

    @Override
    public List<GiftCertificate> findByAnyParams(Long size, String substr, Integer skip, Integer limit, String[] sort) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteriaQuery = criteriaBuilder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> giftCertificateRoot = criteriaQuery.from(GiftCertificate.class);
        Predicate finalPredicate = getFinalPredicate(size, substr, session, criteriaBuilder, giftCertificateRoot);
        if (finalPredicate != null) {
            criteriaQuery.select(giftCertificateRoot).where(finalPredicate).groupBy(giftCertificateRoot.get("id"));
        }
        orderByFieldName(sort, criteriaBuilder, criteriaQuery, giftCertificateRoot);
        List<GiftCertificate> finalList = getPagingList(skip, limit, session, criteriaQuery);
        session.close();
        return finalList;
    }

    @Override
    public int update(Map<String, Object> updates, Integer id, Instant now) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<GiftCertificate> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(GiftCertificate.class);
        Root<GiftCertificate> root = criteriaUpdate.from(GiftCertificate.class);
        updates.forEach(criteriaUpdate::set);
        criteriaUpdate.set("lastUpdateDate", now);
        criteriaUpdate.where(criteriaBuilder.equal(root.get("id"), id));
        session.beginTransaction();
        int size = session.createQuery(criteriaUpdate).executeUpdate();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    @Override
    public Long findSize(Long size, String substr) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<GiftCertificate> giftCertificateRoot = criteriaQuery.from(GiftCertificate.class);
        criteriaQuery.select(criteriaBuilder.count(giftCertificateRoot.get("id")));
        Predicate finalPredicate = getFinalPredicate(size, substr, session, criteriaBuilder, giftCertificateRoot);
        if (finalPredicate != null) {
            criteriaQuery.where(finalPredicate);
        }
        Long total = session.createQuery(criteriaQuery).getSingleResult();
        session.close();
        return total;
    }

    private Predicate getFinalPredicate(Long size, String substr, Session session, CriteriaBuilder criteriaBuilder,
                                        Root<GiftCertificate> giftCertificateRoot) {
        Predicate finalPredicate = null;
        Predicate giftIdIn = (size != null) ? getGiftIdInPredicate(size, session, criteriaBuilder, giftCertificateRoot) : null;
        Predicate likeNameOrDescription = (substr != null) ? getLikeNameOrDescriptionPredicate(substr, criteriaBuilder,
                giftCertificateRoot) : null;
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
        Expression<String> name = giftCertificateRoot.get("name");
        Expression<String> description = giftCertificateRoot.get("description");
        Predicate likeName = criteriaBuilder.like(name, '%' + substr + '%');
        Predicate likeDescription = criteriaBuilder.like(description, '%' + substr + '%');
        return criteriaBuilder.or(likeName, likeDescription);
    }

    private Predicate getGiftIdInPredicate(Long size, Session session, CriteriaBuilder criteriaBuilder,
                                           Root<GiftCertificate> giftCertificateRoot) {
        // Find tag_id
        CriteriaQuery<Integer> findTagId = criteriaBuilder.createQuery(Integer.class);
        Root<Tag> tagRoot = findTagId.from(Tag.class);
        Root<SearchTags> searchTagsRoot = findTagId.from(SearchTags.class);
        findTagId.select(tagRoot.get("id"))
                .where(criteriaBuilder.equal(tagRoot.get("name"), searchTagsRoot.get("name")));
        List<Integer> tagIdList = session.createQuery(findTagId).getResultList();

        // Find gift_id
        CriteriaQuery<Integer> findGiftId = criteriaBuilder.createQuery(Integer.class);
        Root<GiftsTags> root = findGiftId.from(GiftsTags.class);
        Predicate where = root.get("tagId").in(tagIdList);
        Predicate having = criteriaBuilder.equal(criteriaBuilder.count(root.get("giftId")), size);
        findGiftId.select(root.get("giftId")).where(where).groupBy(root.get("giftId")).having(having);
        List<Integer> giftIdList = session.createQuery(findGiftId).getResultList();

        // Predicate giftIdIn
        Expression<Integer> giftId = giftCertificateRoot.get("id");
        return giftId.in(giftIdList);
    }

    private List<GiftCertificate> getPagingList(Integer skip, Integer limit, Session session,
                                                CriteriaQuery<GiftCertificate> criteriaQuery) {
        TypedQuery<GiftCertificate> typedQuery = session.createQuery(criteriaQuery);
        typedQuery.setFirstResult(skip);
        typedQuery.setMaxResults(limit);
        List<GiftCertificate> finalList = typedQuery.getResultList();
        return finalList;
    }

    private void orderByFieldName(String[] sort, CriteriaBuilder criteriaBuilder,
                                  CriteriaQuery<GiftCertificate> criteriaQuery, Root<GiftCertificate> giftCertificateRoot) {
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < sort.length; i++) {
            if (sort[i].startsWith("-")) {
                orderList.add(criteriaBuilder.desc(giftCertificateRoot.get(sort[i].substring(1))));
            } else {
                orderList.add(criteriaBuilder.asc(giftCertificateRoot.get(sort[i])));
            }
        }
        criteriaQuery.orderBy(orderList);
    }

}
