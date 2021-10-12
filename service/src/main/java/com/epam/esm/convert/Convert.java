package com.epam.esm.convert;

import org.springframework.stereotype.Component;

@Component
public interface Convert<T, M> {

    T convertToEntity(M m);
}
