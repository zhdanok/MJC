package com.epam.esm.convert;

import org.springframework.stereotype.Component;

/**
 * Convert from Dto to Entity
 * @param <M> Dto
 * @param <T> Entity
 */
@Component
public interface Converter<T, M> {

    T convertToEntity(M m);
}
