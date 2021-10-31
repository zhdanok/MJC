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

	/**
	 * Save Tag which is parameter of searching for GiftCertificates in SearchTags Table
	 */
	@Override
	public void save(SearchTags searchTags) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(searchTags);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Delete all Tags which were parameters of searching for GiftCertificates from
	 * SearchTags Table
	 */
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
