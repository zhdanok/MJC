package com.epam.esm.repository;

import com.epam.esm.entity.SearchTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SearchTagRepository extends JpaRepository<SearchTags, Integer> {

	/**
	 * Delete all {@link SearchTags} which were parameters of searching for GiftCertificates from
	 * SearchTags Table
	 */
	@Modifying
	@Transactional
	@Query(value = "TRUNCATE TABLE searchtags", nativeQuery = true)
	void clear();

}
