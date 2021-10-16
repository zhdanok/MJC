package com.epam.esm.service;

import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagDao tagDao;

    private final Converter<Tag, TagDto> converter;

    @Transactional
    public void save(TagDto tagDto) {
        Tag tag = converter.convertToEntity(tagDto);
        tagDao.save(tag);
    }

    public List<TagDto> getTags() {
        List<TagDto> list = tagDao.findAll();
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Tags not found");
        }
        return list;
    }

    public List<TagDto> getTagById(Integer id) {
        if (id <= 0) {
            throw new BadRequestException(String.format("Invalid id --> %d", id));
        }
        List<TagDto> list = tagDao.findById(id);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Tag Not Found: id --> %d", id));
        }
        return list;
    }

    @Transactional
    public void deleteById(Integer id) {
        if (id <= 0) {
            throw new BadRequestException(String.format("Invalid id --> %d", id));
        }
        int size = tagDao.deleteById(id);
        if (size == 0) {
            throw new ResourceNotFoundException(String.format("No Tag Found to delete: id --> %d", id));
        }
    }
}
