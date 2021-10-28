package com.epam.esm.convert;

import org.springframework.stereotype.Component;

/**
 * Convert from Dto to Entity
 *
 * @param <D> Dto
 * @param <E> Entity
 */
@Component
public interface Converter<E, D> {

    E convertToEntity(D d);

    D convertToDto(E e);
}
