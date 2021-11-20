package com.epam.esm.util;

import com.epam.esm.exception.AccessDeniedCustomException;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;

public class ExceptionUtils {

	public static final String ERR_CODE_GIFT = "01";

	public static final String ERR_CODE_TAG = "02";

    public static final String ERR_CODE_USER = "03";

    public static final String ERR_CODE_ORDER = "04";

    public static void checkForNotFoundException(boolean expression, String s, String errCode) {
        if (expression) {
            throw new ResourceNotFoundException(s, errCode);
        }
    }

    public static void checkForBadRequestException(boolean expression, String s, String errCode) {
        if (expression) {
            throw new BadRequestException(s, errCode);
        }
    }

    public static void checkForAccessDeniedCustomException(boolean expression, String s, String errCode) {
        if (expression) {
            throw new AccessDeniedCustomException(s, errCode);
        }
    }

}
