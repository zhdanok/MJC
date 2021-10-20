package com.epam.esm.repository;

import com.epam.esm.entity.SearchTag;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchTagDao {

    void save(SearchTag searchTag);

    void clear();
}
