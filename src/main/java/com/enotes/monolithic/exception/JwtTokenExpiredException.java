package com.enotes.monolithic.exception;

public class JwtTokenExpiredException extends RuntimeException {

	public JwtTokenExpiredException(String message) {
		super(message);
	}

}