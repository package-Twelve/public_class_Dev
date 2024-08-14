package com.sparta.publicclassdev.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ExceptionResponse {
	Integer statusCode;
	String message;
}
