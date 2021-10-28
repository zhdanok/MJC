package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

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
    void getLastPage() {
        Long limit = 3L;
        Long sizeOfList = 18L;
        Long a = (sizeOfList % limit) > 0 ? sizeOfList / limit + 1 : sizeOfList / limit;
        Assertions.assertEquals(6, a);

    }

}
