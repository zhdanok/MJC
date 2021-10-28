package com.epam.esm.util;

import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;

public class checkUtil {

    public static void checkForNotFoundException(boolean expression, String s) {
        if (expression) {
            throw new ResourceNotFoundException(s);
        }
    }

    public static void checkForBadRequestException(boolean expression, String s) {
        if (expression) {
            throw new BadRequestException(s);
        }
    }

}
