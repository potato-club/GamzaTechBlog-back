package org.gamja.gamzatechblog.common.dto;

import java.util.Optional;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class ResponseDto<T> {
	private final int status;
	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T data;
	private final long timestamp;

	public ResponseDto(int status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
	}

	public static <T> ResponseDto<T> of(HttpStatus httpStatus, String message) {
		int status = Optional.ofNullable(httpStatus)
			.orElse(HttpStatus.OK)
			.value();
		return new ResponseDto<>(status, message, null);
	}

	public static <T> ResponseDto<T> of(HttpStatus httpStatus, String message, T data) {
		int status = Optional.ofNullable(httpStatus)
			.orElse(HttpStatus.OK)
			.value();
		return new ResponseDto<>(status, message, data);
	}
}