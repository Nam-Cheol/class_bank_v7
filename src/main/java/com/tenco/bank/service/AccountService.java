package com.tenco.bank.service;

import java.util.ArrayList;
import java.util.List;

import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.HistoryAccount;
import com.tenco.bank.utils.Define;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final HistoryRepository historyRepository;

	/**
	 * 계좌 생성 기능
	 *
	 * @param dto
	 * @param id
	 */
	@Transactional
	public void createAccount(SaveDTO dto, User user) {
		Account account = dto.toAccount(user);
		try {
			accountRepository.save(account); // JPA의 save 메서드를 사용
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@Transactional
	public List<Account> readAccountListByUserId(Integer principalId) {
		List<Account> accountList = new ArrayList<>();

		try {
			accountList = accountRepository.findByUserId(principalId); // JPA 메서드 사용
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}

		return accountList;
	}

	@Transactional
	public void updateAccountWithdraw(WithdrawalDTO dto, Integer principalId) {

		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());

		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}

		accountEntity.checkOwner(principalId);
		accountEntity.checkPassword(dto.getWAccountPassword());
		accountEntity.checkBalance(dto.getAmount());

		accountEntity.withDraw(dto.getAmount());
		accountRepository.save(accountEntity); // JPA의 save 메서드를 사용하여 업데이트

		historyRepository.save(History.builder()
				.amount(dto.getAmount())
				.wBalance(accountEntity.getBalance())
				.wAccount(accountEntity)
				.build()); // JPA에서 insert는 save 메서드로 처리
	}

	@Transactional
	public void updateAccountDeposit(DepositDTO dto, Integer principalId) {

		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());

		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}

		accountEntity.checkOwner(principalId);
		accountEntity.deposit(dto.getAmount());

		accountRepository.save(accountEntity); // JPA의 save 메서드를 사용하여 업데이트
		historyRepository.save(History.builder()
				.amount(dto.getAmount())
				.dAccount(accountEntity)
				.dBalance(accountEntity.getBalance())
				.build());
	}

	@Transactional
	public void updateAccountTransfer(TransferDTO dto, Integer principalId) {

		Account wAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if (wAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}

		Account dAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		if (dAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}

		wAccountEntity.checkOwner(principalId);
		wAccountEntity.checkPassword(dto.getPassword());
		wAccountEntity.checkBalance(dto.getAmount());
		wAccountEntity.withDraw(dto.getAmount());
		dAccountEntity.deposit(dto.getAmount());

		accountRepository.save(wAccountEntity); // JPA의 save 메서드를 사용하여 업데이트
		accountRepository.save(dAccountEntity);

		historyRepository.save(History.builder()
				.amount(dto.getAmount())
				.wAccount(wAccountEntity)
				.wBalance(wAccountEntity.getBalance())
				.dAccount(dAccountEntity)
				.dBalance(dAccountEntity.getBalance())
				.build());
	}

	public Account readAccountById(Integer accountId) {
		Account accountEntity = accountRepository.findById(accountId)
				.orElseThrow(() -> new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR));
		return accountEntity;
	}

	/**
	 * 단일 계좌 거래 내역 조회
	 *
	 * @param type      = [all, deposit, withdrawal]
	 * @param accountId (pk)
	 * @return 전체, 입금, 출금 거래 내역 (3가지 타입) 반환
	 */
	@Transactional
	public List<HistoryAccount> readHistoryByAccountId(String type, Integer accountId, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		List<HistoryAccount> list = new ArrayList<>();
		switch (type) {
			case "all":
				list = historyRepository.findByAccountIdAndTypeOfHistoryAll(accountId, pageable);
				break;
			case "deposit":
				list = historyRepository.findByAccountIdAndTypeOfHistoryDeposit(accountId, pageable);
				break;
			case "withdrawal":
				list = historyRepository.findByAccountIdAndTypeOfHistoryWithdrawal(accountId, pageable);
				break;
			default:
				throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.BAD_REQUEST);
		}

		return list;
	}

	// 해당 계좌와 거래 유형에 따른 전체 레코드 수를 반환하는 메소드
	@Transactional
	public int countHistoryByAccountIdAndType(String type, Integer accountId) {
		int count = 0;
		switch (type) {
			case "all":
				count = historyRepository.countByAccountIdAndTypeAll(accountId);
				break;
			case "deposit":
				count = historyRepository.countByAccountIdAndTypeDeposit(accountId);
				break;
			case "withdrawal":
				count = historyRepository.countByAccountIdAndTypeWithdrawal(accountId);
				break;
			default:
				throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.BAD_REQUEST);
		}

		return count;
	}
}
