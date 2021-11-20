package com.epam.esm.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;

@Getter
@Setter
public class AccessDeniedCustomException extends AccessDeniedException {

	private String errCode;

	public AccessDeniedCustomException(String message, String errCode) {

		super(message);
		this.errCode = errCode;
	}

}
