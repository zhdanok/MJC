package com.epam.esm.service;

import com.epam.esm.config.MapperConfig;
import com.epam.esm.convert.Convert;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ServiceTestConfig.class, MapperConfig.class})
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    Convert<Tag, TagDto> convert;

    @Autowired
    TagDao tagDao;

    @Test
    void getTags() {
        List<TagDto> expList = new ArrayList<>();
        expList.add(TagDto.builder().id(1).name("tag1").build());
        expList.add(TagDto.builder().id(2).name("tag2").build());
        when(tagDao.findAll()).thenReturn(expList);
        List<TagDto> actualResult = tagService.getTags();
        assertThat(actualResult).usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(expList);
    }

    @Test
    void getTagById() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void findTagByName() {
    }
}