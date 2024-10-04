package com.tenco.bank.repository.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.HistoryAccount;

public interface HistoryRepository extends JpaRepository<History, Integer> {

	// 거래내역 조회
	@Query("SELECT h FROM History h WHERE h.id = :id")
	Optional<History> findById(@Param("id") Integer id);

	@Query("SELECT h FROM History h")
	List<History> findAll();

	// 'all' 타입일 경우
	@Query("SELECT new com.tenco.bank.repository.model.HistoryAccount(h.id, h.amount, " +
			"CASE WHEN h.wAccount.id = :accountId THEN h.wBalance " +
			"WHEN h.dAccount.id = :accountId THEN h.dBalance END, " +
			"COALESCE(wa.number, 'ATM'), COALESCE(da.number, 'ATM'), h.createdAt) " +
			"FROM History h " +
			"LEFT JOIN h.wAccount wa " +
			"LEFT JOIN h.dAccount da " +
			"WHERE h.wAccount.id = :accountId OR h.dAccount.id = :accountId " +
			"ORDER BY h.createdAt DESC")
	List<HistoryAccount> findByAccountIdAndTypeOfHistoryAll(
			@Param("accountId") Integer accountId, Pageable pageable);

	// 'deposit' 타입일 경우
	@Query("SELECT new com.tenco.bank.repository.model.HistoryAccount(h.id, h.amount, h.dBalance, " +
			"COALESCE(wa.number, 'ATM'), COALESCE(da.number, 'ATM'), h.createdAt) " +
			"FROM History h " +
			"LEFT JOIN h.wAccount wa " +
			"LEFT JOIN h.dAccount da " +
			"WHERE h.dAccount.id = :accountId " +
			"ORDER BY h.createdAt DESC")
	List<HistoryAccount> findByAccountIdAndTypeOfHistoryDeposit(
			@Param("accountId") Integer accountId, Pageable pageable);

	// 'withdrawal' 타입일 경우
	@Query("SELECT new com.tenco.bank.repository.model.HistoryAccount(h.id, h.amount, h.wBalance, " +
			"COALESCE(wa.number, 'ATM'), COALESCE(da.number, 'ATM'), h.createdAt) " +
			"FROM History h " +
			"LEFT JOIN h.wAccount wa " +
			"LEFT JOIN h.dAccount da " +
			"WHERE h.wAccount.id = :accountId " +
			"ORDER BY h.createdAt DESC")
	List<HistoryAccount> findByAccountIdAndTypeOfHistoryWithdrawal(
			@Param("accountId") Integer accountId, Pageable pageable);

	// 계좌 ID와 타입에 따른 거래 내역 개수 조회 (all)
	@Query("SELECT COUNT(h) FROM History h " +
			"WHERE h.wAccount.id = :accountId OR h.dAccount.id = :accountId")
	int countByAccountIdAndTypeAll(@Param("accountId") Integer accountId);

	// 계좌 ID와 타입에 따른 거래 내역 개수 조회 (deposit)
	@Query("SELECT COUNT(h) FROM History h WHERE h.dAccount.id = :accountId")
	int countByAccountIdAndTypeDeposit(@Param("accountId") Integer accountId);

	// 계좌 ID와 타입에 따른 거래 내역 개수 조회 (withdrawal)
	@Query("SELECT COUNT(h) FROM History h WHERE h.wAccount.id = :accountId")
	int countByAccountIdAndTypeWithdrawal(@Param("accountId") Integer accountId);
}
