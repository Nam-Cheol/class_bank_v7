package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

// 에러 발생 시에 여러 페이지르로 이동 시킬 때 사용 예정
@Getter
public class RedirectException extends RuntimeException{

	private HttpStatus status;
	public RedirectException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
}
