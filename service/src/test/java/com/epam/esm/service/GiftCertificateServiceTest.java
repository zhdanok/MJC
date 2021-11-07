package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.repository.GiftCertificateDao;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.abstraction.MockUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.andreinc.mockneat.unit.objects.Reflect.reflect;
import static net.andreinc.mockneat.unit.text.Words.words;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = ServiceApplication.class)
@TestPropertySource("classpath:test.properties")
class GiftCertificateServiceTest {

    @Value("${populate.database}")
    private boolean isNeedPopulateBd;

    @Autowired
    GiftCertificateService service;

    @Autowired
    Converter<GiftCertificate, GiftAndTagDto> converterForGift;

    @MockBean
    GiftCertificateDao dao;

	@Test
	void getCertificateById() {
		// given
		Integer id = 2;
		GiftCertificate mockGift = GiftCertificate.builder().id(id).name("Gift1").price(125.3).duration(125)
				.description("new").build();
		GiftAndTagDto expGift = GiftAndTagDto.builder().id(id).name("Gift1").price(125.3).duration(125)
				.description("new").build();

		// when
		when(dao.findById(id)).thenReturn(mockGift);
		GiftAndTagDto actual = service.getCertificateById(id);

		// then
		assertEquals(expGift, actual);
	}

	@Test
	void getCertificatesByAnyParams() {
		// given
		List<GiftCertificate> mockList = getMockList();
		List<GiftAndTagDto> expList = getExpList();
		Integer page = 2;
		Integer limit = 2;
		Integer skip = 2;
		String[] sort = new String[]{"id"};

		// when
		when(dao.findByAnyParams(null, null, skip, limit, sort)).thenReturn(mockList);
		List<GiftAndTagDto> actualList = service.getCertificatesByAnyParams(null, null, sort, page, limit);

		// then
		assertEquals(expList, actualList);
	}

	@Test
	void deleteById_withNotFound() {
		// given
		Integer id = 75;
		String expected = String.format("No Certificates Found to delete: id --> %d", id);

		// when
		when(dao.deleteById(id)).thenReturn(0);
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> service.deleteById(id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	@Test
	void update_withNotFound() {
		// given
		Integer id = 75;
		Map<String, Object> updates = new HashMap<>();
		String expected = String.format("No Certificates Found to update: id --> %d", id);

		// when
		when(dao.update(updates, id, Instant.now())).thenReturn(0);
		Exception ex = assertThrows(ResourceNotFoundException.class, () -> service.update(updates, id));

		// then
		assertEquals(expected, ex.getMessage());
	}

	private List<GiftCertificate> getMockList() {
		List<GiftCertificate> mockList = new ArrayList<>();
		mockList.add(
				GiftCertificate.builder().id(1).name("Gift1").price(125.3).duration(125).description("new1").build());
		mockList.add(
				GiftCertificate.builder().id(2).name("Gift2").price(125.5).duration(56).description("new2").build());
		mockList.add(
				GiftCertificate.builder().id(3).name("Gift3").price(125.7).duration(75).description("new3").build());

		return mockList;
	}

	private List<GiftAndTagDto> getExpList() {
		List<GiftAndTagDto> expList = new ArrayList<>();
		expList.add(GiftAndTagDto.builder().id(1).name("Gift1").price(125.3).duration(125).description("new1").build());
		expList.add(GiftAndTagDto.builder().id(2).name("Gift2").price(125.5).duration(56).description("new2").build());
		expList.add(GiftAndTagDto.builder().id(3).name("Gift3").price(125.7).duration(75).description("new3").build());
		return expList;
	}

    /**
     * This test can generate 10000 new GiftCertificates with their Tags and save it to
     * database. If You need to generate it, please change populate.database to true in
     * test.properties and then run the test
     */
    @Test
    void loadDataToTable() {
        if (isNeedPopulateBd) {
            MockNeat mockNeat = MockNeat.threadLocal();
            for (int i = 0; i < 10000; i++) {
                MockUnit<GiftCertificate> rGiftGenerator = reflect(GiftCertificate.class).field("name", words().nouns())
                        .field("description", words().nouns()).field("price", mockNeat.doubles().range(0.10, 300.00))
                        .field("duration", mockNeat.ints().range(1, 365)).field("tags", mockNeat.reflect(Tag.class)
                                .field("name", words().nouns()).set(mockNeat.ints().range(0, 3)));

                service.save(converterForGift.convertToDto(rGiftGenerator.get()));

            }
        }
    }

}
