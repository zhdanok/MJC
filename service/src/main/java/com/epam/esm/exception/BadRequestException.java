package com.epam.esm.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadRequestException extends RuntimeException {

	private String errCode;

	public BadRequestException(String message, String errCode) {

		super(message);
		this.errCode = errCode;
	}

}
