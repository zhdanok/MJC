package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.TagDao;
import com.epam.esm.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	@MockBean
	TagRepository tagRepository;

	@Test
	void save() {
		// given
		Integer expId = 2;
		TagDto dto = TagDto.builder().name("tag2").build();
		Tag mockTag = Tag.builder().id(expId).name("tag2").build();

		// when
		when(tagRepository.findTagByName(dto.getName()).orElseGet(() -> tagRepository.save(converter.convertToEntity(dto)))).thenReturn(mockTag);
		Integer actualId = tagService.save(dto);

		// then
		assertEquals(expId, actualId);
	}

	@Test
	void getTags() {
		// given
		Page<Tag> mockList = getMockList();
		Page<TagDto> expList = getExpList();
		Pageable pageable = PageRequest.of(2, 2);

		// when
		when(tagRepository.findAll(pageable)).thenReturn(mockList);
		Page<TagDto> actualList = tagService.getTags(pageable);

		// then
		assertEquals(expList, actualList);
	}

	@Test
	void getTags_withNotFound() {
		// given
		Pageable pageable = PageRequest.of(2, 2);
		String expected = "Tags not found";

		// when
		when(tagRepository.findAll(pageable)).thenReturn(Page.empty());
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> tagService.getTags(pageable));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void getTagById() {
		// given
		Integer id = 2;
		Tag mockTag = Tag.builder().id(id).name("tag2").build();
		TagDto expTag = TagDto.builder().id(id).name("tag2").build();

		// when
		when(tagRepository.findById(id)).thenReturn(Optional.of(mockTag));
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

	private Page<Tag> getMockList() {
		List<Tag> mockList = new ArrayList<>();
		mockList.add(Tag.builder().id(1).name("tag1").build());
		mockList.add(Tag.builder().id(2).name("tag2").build());
		mockList.add(Tag.builder().id(3).name("tag3").build());
		return new PageImpl<>(mockList);
	}

	private Page<TagDto> getExpList() {
		List<TagDto> expList = new ArrayList<>();
		expList.add(TagDto.builder().id(1).name("tag1").build());
		expList.add(TagDto.builder().id(2).name("tag2").build());
		expList.add(TagDto.builder().id(3).name("tag3").build());
		return new PageImpl<>(expList);
	}

}
