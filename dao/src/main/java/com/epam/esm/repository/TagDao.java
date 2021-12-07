package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;

@Repository
public interface TagDao {

	/**
	 * Send request for getting the most widely used {@link com.epam.esm.entity.Tag} of a user with the highest cost
	 * of all orders
	 *
	 * @return Tag
	 */
	Tag findMostPopularTagOfUserWithHighestCostOfOrder();
}
