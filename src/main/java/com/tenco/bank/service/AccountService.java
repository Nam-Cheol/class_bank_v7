package com.tenco.bank.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.model.Account;

@Service
public class AccountService {

	private final AccountRepository accountRepository;
	
	@Autowired
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}
	
	/**
	 * 계좌 생성 기능
	 * @param dto
	 * @param id
	 */
	@Transactional
	public void createAccount(SaveDTO dto, Integer id) {
		
		int result = 0;
		
		try {
			result = accountRepository.insert(dto.toAccount(id));
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 요청입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류입니다.", HttpStatus.SERVICE_UNAVAILABLE);
			
		}
		
		if(result == 0) {
			throw new DataDeliveryException("정상 처리되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	public List<Account> readAccountListByUserId(@Param("userId") Integer principalId) {
		List<Account> accountListEntity = null;
		
		try {
			accountListEntity = accountRepository.findByUserId(principalId);
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 처리입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		return accountListEntity;
	}

}
