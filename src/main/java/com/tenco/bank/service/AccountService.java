package com.tenco.bank.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.TransferDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.HistoryAccount;
import com.tenco.bank.utils.Define;

@Service
public class AccountService {

	private final AccountRepository accountRepository;
	private final HistoryRepository historyRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository, HistoryRepository historyRepository) {
		this.accountRepository = accountRepository;
		this.historyRepository = historyRepository;
	}

	/**
	 * 계좌 생성 기능
	 * 
	 * @param dto
	 * @param id
	 */
	@Transactional
	public void createAccount(SaveDTO dto, Integer id) {

		int result = 0;

		try {
			result = accountRepository.insert(dto.toAccount(id));
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);

		}

		if (result == 0) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public List<Account> readAccountListByUserId(@Param("userId") Integer principalId) {
		List<Account> accountListEntity = null;

		try {
			accountListEntity = accountRepository.findByUserId(principalId);
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}

		return accountListEntity;
	}

	// 한 번에 모든 기능을 생각하는 것은 힘들다.
	// 1. 사용자가 입력한 계좌 번호가 존재하는 지 여부 확인 -- select
	// 2. 계좌번호가 사용자의 계좌인지 확인 -- 객체 상태값에서 비교 Why? 1번에서 가져온 정보에 다 담겨있다.
	// 3. 계좌 비번 확인 -- 객체 상태값에서 일치 여부 확인
	// 4. 잔액 여부 확인 -- 객체 상태값에서 확인
	// 5. 출금 처리 -- update
	// 6. 거래 내역 등록 -- insert(history)
	// 7. 트랜잭션 처리
	@Transactional
	public void updateAccountWithdraw(WithdrawalDTO dto, Integer principalId) {

		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());

		// 1
//		accountEntity.checkAccount(accountEntity);
		if(accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		// 2
		accountEntity.checkOwner(principalId);
		// 3
		accountEntity.checkPassword(dto.getWAccountPassword());
		// 4
		accountEntity.checkBalance(dto.getAmount());
		// 5
		accountEntity.withDraw(dto.getAmount());
		// 6
		accountRepository.updateById(accountEntity);

		//INSERT INTO history_tb(amount, w_account_id, d_account_id, w_balance, d_balance)
		//VALUES (#{amount}, #{wAccount}, #{dAccount}, #{wBalance}, #{dBalance})
		//7
		
		int rowResultCount = historyRepository.insert(History.builder()
														.amount(dto.getAmount())
														.wBalance(accountEntity.getBalance())
														.wAccount(accountEntity.getId())
														.build());
												
		if(rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 입금 기능 만들기
	// 1. 입금계좌 유효
	// 2. 입금 금액 0 이상인지
	@Transactional
	public void updateAccountDeposit(DepositDTO dto, Integer principalId) {
		
		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		
		if(accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		
		accountEntity.checkOwner(principalId);
		accountEntity.deposit(dto.getAmount());
		
		accountRepository.updateById(accountEntity);
		int rowCount = historyRepository.insert(History.builder()
									.amount(dto.getAmount())
									.dAccount(accountEntity.getId())
									.dBalance(accountEntity.getBalance())
									.build());
		
		if(rowCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	// 이체 기능 만들기
	// 1. 출금 계좌 존재 여부 확인 select
	// 2. 입금 계좌 존재 여부 확인 select (객체 리턴 받은 상태)
	// 3. 출금 계좌 본인 소유 확인 -- 객체 상태값과 세션 Id 비교
	// 4. 출금 계좌 비밀 번호 확인 -- 객체 상태값과 비교 dto 비밀번호 비교
	// 5. 출금 계좌 잔액 확인 -- 객체 상태값 확인, dto와 비교
	// 6. 입금 계좌 객체 상태값 변경 처리 (거래금액 증가 처리)
	// 7. 입금 계좌 update 처리
	// 8. 출금 계좌 객체 상태값 변경 처리 (잔액 - 거래금액)
	// 9. 출금 계좌 update 처리
	// 10. history 거래 내역 등록 처리
	// 11. 트랜잭션 처리
	@Transactional
	public void updateAccountTransfer(TransferDTO dto, Integer principalId) {
		
		Account wAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if(wAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		
		Account dAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		if(dAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		
		wAccountEntity.checkOwner(principalId);
		wAccountEntity.checkPassword(dto.getPassword());
		wAccountEntity.checkBalance(dto.getAmount());
		wAccountEntity.withDraw(dto.getAmount());
		dAccountEntity.deposit(dto.getAmount());
		
		int wRowCount = accountRepository.updateById(wAccountEntity);
		int dRowCount = accountRepository.updateById(dAccountEntity);
		
		if(wRowCount != 1 && dRowCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.BAD_REQUEST);
		}
		
		int rowHistory = historyRepository.insert(History.builder()
									.amount(dto.getAmount())
									.wAccount(wAccountEntity.getId())
									.wBalance(wAccountEntity.getBalance())
									.dAccount(dAccountEntity.getId())
									.dBalance(dAccountEntity.getBalance())
									.build());
		
		if(rowHistory != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 단일 계좌 조회 기능 (accountId 기준)
	 * @param accountId (pk)
	 * @return 단일 계좌 상세 내역
	 */
	public Account readAccountById(Integer accountId) {
		Account accountEntity = accountRepository.findByAccountId(accountId);
		
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return accountEntity;
	}

	/**
	 * 단일 계좌 거래 내역 조회
	 * @param type = [all, deposit, withdrawal]
	 * @param accountId (pk)
	 * @return 전체, 입금, 출금 거래 내역 (3가지 타입) 반환
	 */
//	@Transactional >> 거래내역은 insert 등이 빈번하게 일어나서 팬텀리드 현상 발생 가능성 多
	public List<HistoryAccount> readHistoryByAccountId(String type, Integer accountId) {
		List<HistoryAccount> list = new ArrayList<>();
		list = historyRepository.findByAccountIdAndTypeOfHistory(type, accountId);
		
		return list;
	}

}
