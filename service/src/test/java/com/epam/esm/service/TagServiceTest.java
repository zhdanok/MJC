package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.TagDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	void save() {
		// given
		Integer expId = 2;
		TagDto dto = TagDto.builder().name("tag2").build();
		Tag mockTag = Tag.builder().id(expId).name("tag2").build();

		// when
		when(tagDao.findTagIdByTagName(mockTag.getName())).thenReturn(mockTag.getId());
		Integer actualId = tagService.save(dto);

		// then
		assertEquals(expId, actualId);
	}

	@Test
	void getTags() {
		// given
		List<Tag> mockList = getMockList();
		List<TagDto> expList = getExpList();
		Integer page = 2;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;

		// when
		when(tagDao.findAll(skip, limit)).thenReturn(mockList);
		when(tagDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		List<TagDto> actualList = tagService.getTags(page, limit);

		// then
		assertEquals(expList, actualList);
	}

	@Test
	void getTags_withInvalidPage() {
		// given
		List<Tag> mockList = getMockList();
		Integer page = 5;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;
		String expected = String.format("Invalid page --> %d", page);

		// when
		when(tagDao.findAll(skip, limit)).thenReturn(mockList);
		when(tagDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Exception ex = assertThrows(BadRequestException.class, () -> tagService.getTags(page, limit));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getTags_withInvalidLimit() {
		// given
		List<Tag> mockList = getMockList();
		Integer page = 1;
		Integer limit = -7;
		Integer skip = (page - 1) * limit;
		String expected = String.format("Invalid limit --> %d", limit);

		// when
		when(tagDao.findAll(skip, limit)).thenReturn(mockList);
		when(tagDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Exception ex = assertThrows(BadRequestException.class, () -> tagService.getTags(page, limit));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getTags_withNotFound() {
		// given
		List<Tag> mockList = getMockList();
		Integer page = 1;
		Integer limit = 2;
		Integer skip = (page - 1) * limit;
		String expected = "Tags not found";

		// when
		when(tagDao.findAll(skip, limit)).thenReturn(Collections.EMPTY_LIST);
		when(tagDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> tagService.getTags(page, limit));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getLastPage() {
		// given
		List<Tag> mockList = getMockList();
		Integer limit = 2;
		Long expected = 2L;

		// when
		when(tagDao.findSize()).thenReturn(Long.valueOf(mockList.size()));
		Long actual = tagService.getLastPage(limit);

		// then
		assertEquals(expected, actual);

	}

	@Test
	void getTagById() {
		// given
		Integer id = 2;
		Tag mockTag = Tag.builder().id(id).name("tag2").build();
		TagDto expTag = TagDto.builder().id(id).name("tag2").build();

		// when
		when(tagDao.findById(id)).thenReturn(mockTag);
		TagDto actual = tagService.getTagById(id);

		// then
		assertEquals(expTag, actual);
	}

	@Test
	void getTagById_withInvalidId() {
		// given
		Integer id = -7;
		String expected = String.format("Invalid id --> %d", id);

		// when
		Exception ex = assertThrows(BadRequestException.class, () -> tagService.getTagById(id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getTagById_withNotFound() {
		// given
		Integer id = 75;
		String expected = String.format("Tag Not Found: id --> %d", id);

		// when
		when(tagDao.findById(id)).thenReturn(null);
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void deleteTagById_withInvalidId() {
		// given
		Integer id = -7;
		String expected = String.format("Invalid id --> %d", id);

		// when
		Exception ex = assertThrows(BadRequestException.class, () -> tagService.deleteById(id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void deleteTagById_withNotFound() {
		// given
		Integer id = 75;
		String expected = String.format("No Tag Found to delete: id --> %d", id);

		// when
		when(tagDao.deleteById(id)).thenReturn(0);
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> tagService.deleteById(id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getMostPopularTagOfUserWithHighestCostOfOrder() {
		// given
		Tag mockTag = Tag.builder().name("kookaburras").build();
		TagDto expTag = TagDto.builder().name("kookaburras").build();

		// when
		when(tagDao.findMostPopularTagOfUserWithHighestCostOfOrder()).thenReturn(mockTag);
		TagDto actual = tagService.getMostPopularTagOfUserWithHighestCostOfOrder();

		// then
		assertEquals(expTag, actual);
	}

	@Test
	void getMostPopularTagOfUserWithHighestCostOfOrder_withNotFound() {
		// given
		String expected = "There are no Tags";

		// when
		when(tagDao.findMostPopularTagOfUserWithHighestCostOfOrder()).thenReturn(null);
		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> tagService.getMostPopularTagOfUserWithHighestCostOfOrder());

		// then
		assertEquals(expected, ex.getMessage());
	}

	private List<Tag> getMockList() {
		List<Tag> mockList = new ArrayList<>();
		mockList.add(Tag.builder().id(1).name("tag1").build());
		mockList.add(Tag.builder().id(2).name("tag2").build());
		mockList.add(Tag.builder().id(3).name("tag3").build());
		return mockList;
	}

	private List<TagDto> getExpList() {
		List<TagDto> expList = new ArrayList<>();
		expList.add(TagDto.builder().id(1).name("tag1").build());
		expList.add(TagDto.builder().id(2).name("tag2").build());
		expList.add(TagDto.builder().id(3).name("tag3").build());
		return expList;
	}

}
