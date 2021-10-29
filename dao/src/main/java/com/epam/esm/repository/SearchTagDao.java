package com.epam.esm.repository;

import com.epam.esm.entity.SearchTags;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchTagDao {

	void save(SearchTags searchTags);

    void clear();

}
