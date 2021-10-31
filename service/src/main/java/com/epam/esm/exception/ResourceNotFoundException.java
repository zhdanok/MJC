package com.epam.esm.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

	private String errCode;

	public ResourceNotFoundException(String message, String errCode) {

		super(message);
		this.errCode = errCode;
	}

}
