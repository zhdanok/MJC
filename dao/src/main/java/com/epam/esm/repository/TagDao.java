package com.epam.esm.repository;

import com.epam.esm.dto.TagDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagDao {

    void save(TagDto tag);

    List<TagDto> findAll();

    List<TagDto> findById(Integer id);

    Integer findTagIdByTagName(String name);

    int deleteById(Integer id);

    List<TagDto> findMostPopularTagOfUserWithHighestCostOfOrder();
}
