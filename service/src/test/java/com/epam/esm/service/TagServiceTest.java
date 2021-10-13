package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = ServiceApplication.class)
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    Converter<Tag, TagDto> converter;

    @MockBean
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

}