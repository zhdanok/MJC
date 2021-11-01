package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagDao {

	void save(Tag tag);

	List<Tag> findAll(Integer skip, Integer limit);

	Tag findById(Integer id);

	Integer findTagIdByTagName(String name);

	int deleteById(Integer id);

	Long findSize();

	Tag findMostPopularTagOfUserWithHighestCostOfOrder();

}
