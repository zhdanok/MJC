package com.epam.esm.service;

import com.epam.esm.repository.TagDao;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceTestConfig {
    @Bean
    @Primary
    public TagDao nameService() {
        return Mockito.mock(TagDao.class);
    }
}
