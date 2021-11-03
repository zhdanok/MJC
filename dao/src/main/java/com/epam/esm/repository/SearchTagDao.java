package com.epam.esm.repository;

import com.epam.esm.entity.SearchTags;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchTagDao {

	/**
     * Save {@link SearchTags} which is parameter of searching for GiftCertificates in SearchTags Table
     */
	void save(SearchTags searchTags);

    /**
     * Delete all {@link SearchTags} which were parameters of searching for GiftCertificates from
     * SearchTags Table
	 */
	void clear();

}
