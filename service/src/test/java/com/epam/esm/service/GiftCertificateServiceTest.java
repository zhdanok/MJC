package com.epam.esm.service;

import com.epam.esm.ServiceApplication;
import com.epam.esm.convert.Converter;
import com.epam.esm.dto.GiftAndTagDto;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ServiceApplication.class)
class GiftCertificateServiceTest {

    @Autowired
    GiftCertificateService service;

    @Autowired
    Converter<GiftCertificate, GiftAndTagDto> converterForGift;

    /**
     * This test can generate 1300 new GiftCertificates with their Tags and save it to database.
     * Please uncomment it only if you need to generate new data
     */
	/*@Test
	void loadDataToTable() {

		MockNeat mockNeat = MockNeat.threadLocal();

		for (int i = 0; i < 1300; i++) {
			MockUnit<GiftCertificate> rGiftGenerator = reflect(GiftCertificate.class).field("name", words().nouns())
					.field("description", words().nouns()).field("price", mockNeat.doubles().range(0.10, 300.00))
					.field("duration", mockNeat.ints().range(1, 365)).field("tags", mockNeat.reflect(Tag.class)
							.field("name", words().nouns()).set(mockNeat.ints().range(0, 3)));

			service.save(converterForGift.convertToDto(rGiftGenerator.get()));

		}

	}*/

}