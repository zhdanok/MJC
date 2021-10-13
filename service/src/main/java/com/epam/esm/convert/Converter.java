package com.epam.esm.convert;

import org.springframework.stereotype.Component;
/**
 * Convert from Dto to Entity
 * @param <M> Dto
 * @param <T> Entity
 * @return instance of the Entity's class
 */
@Component
public interface Converter<T, M> {

    T convertToEntity(M m);
}
