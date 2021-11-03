package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagDao {

	/**
	 * Send request for saving {@link com.epam.esm.entity.Tag}
	 *
	 * @param tag - Entity which need to save
	 */
	void save(Tag tag);

	/**
	 * Send request for getting {@link com.epam.esm.entity.Tag} with required skip and limit
	 *
	 * @param skip  - count of Tags which need skip
	 * @param limit - count of Tags which need to view at page
	 * @return List of Tags with requirement parameters
	 */
	List<Tag> findAll(Integer skip, Integer limit);

	/**
	 * Send request for getting {@link com.epam.esm.entity.Tag} by Tag's Id
	 *
	 * @param id - id of Tag which need to get
	 * @return Tag
	 */
	Tag findById(Integer id);

	/**
	 * Send request for getting Tag's id by Tag's name
	 *
	 * @param name - name of Tag which id need to get
	 * @return Id
	 */
	Integer findTagIdByTagName(String name);

	/**
	 * Send request for deleting {@link com.epam.esm.entity.Tag} by Tag's Id
	 *
	 * @param id - id of Tag which need to delete
	 * @return size - number which equals 1 if Tag is deleted, and 0 if Tag isn't deleted
	 */
	int deleteById(Integer id);

	/**
	 * Send request for getting count of all {@link com.epam.esm.entity.Tag}
	 *
	 * @return Long
	 */
	Long findSize();

	/**
	 * Send request for getting the most widely used {@link com.epam.esm.entity.Tag} of a user with the highest cost
	 * of all orders
	 *
	 * @return Tag
	 */
	Tag findMostPopularTagOfUserWithHighestCostOfOrder();

}
