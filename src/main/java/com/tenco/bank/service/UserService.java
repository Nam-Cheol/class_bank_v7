package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

@Service // IoC의 대상(싱글톤으로 관리되는 객체)
public class UserService {

	private UserRepository userRepository;
	// 구현 객체가 없는데 어떻게 인터페이스 객체를 생성해주지 ? mapper 때문에 ?
	
	// DI - 의존 주입
	@Autowired
	public UserService(UserRepository repository) {
		this.userRepository = repository;
	}

	/**
	 * 회원 등록 서비스 기능
	 * 트랜잭션 처리
	 * @param dto
	 */
	@Transactional // 트랜잭션 처리는 반드시 습관화
	public void createUser(SignUpDTO dto) {
		int result = 0;
		try {
			result = userRepository.insert(dto.toUser());
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.DUPLICATE_ID, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			// 다른 오류라면
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if (result != 1) {
			throw new DataDeliveryException(Define.FAIL_TO_CREATE_USER, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public User readUser(SignInDTO dto) {
		
		// 유효성 검사는 controller에서 먼저 하자.
		User userEntity = null; // 지역 변수 선언
		
		try {
			userEntity = userRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(userEntity == null) {
			throw new DataDeliveryException(Define.FAIL_TO_LOGIN, HttpStatus.BAD_REQUEST);
		}
		
		return userEntity;
	}

}
