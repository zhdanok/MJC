package com.epam.esm.repository;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagDao {

    @Transactional
    void save(Tag tag);

    List<TagDto> findAll();

    List<TagDto> findById(Integer id);

    @Transactional
    int deleteById(Integer id);

    Integer findByTagName(String tagName);
}
