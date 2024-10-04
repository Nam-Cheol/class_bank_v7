package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tenco.bank.repository.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	// 유저 ID로 계좌 정보 조회
	@Query("SELECT a FROM Account a WHERE a.user.id = :userId")
	List<Account> findByUserId(@Param("userId") Integer principalId);

	// 계좌 번호로 계좌 정보 조회
	@Query("SELECT a FROM Account a WHERE a.number = :number")
	Account findByNumber(@Param("number") String number);

	// JpaRepository가 제공하는 findById로 대체 가능
	// Account findById(Integer accountId);
}
