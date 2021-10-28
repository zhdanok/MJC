package com.epam.esm.repository;

import com.epam.esm.entity.SearchTags;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

@Repository
@RequiredArgsConstructor
public class SearchTagDaoImpl implements SearchTagDao {

    private final SessionFactory sessionFactory;

    @Override
    public void save(SearchTags searchTags) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(searchTags);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void clear() {
        Session session = sessionFactory.openSession();
        Query query = session.createNativeQuery("TRUNCATE TABLE searchtags");
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
