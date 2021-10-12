package com.epam.esm.convert;

public interface Convert<T, M> {

    M convertToDto(T t);

    T convertToEntity(M m);
}
