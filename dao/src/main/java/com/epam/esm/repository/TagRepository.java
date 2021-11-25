package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Integer> {

	Optional<Tag> findTagByName(String name);

	@Query("select t.id from Tag t where t.name = :name")
	Optional<Integer> findTagIdByName(@Param("name") String name);

}
