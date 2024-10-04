package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.utils.Define;
import com.tenco.bank.utils.ValueFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "account_tb") // JPA 테이블 매핑
public class Account extends ValueFormatter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 및 자동 증가 설정
	private Integer id;

	@Column(nullable = false, unique = true, length = 30) // 계좌번호에 대한 제약 조건
	private String number;

	@Column(nullable = false, length = 30)
	private String password;

	@Column(nullable = false)
	private Long balance;

	@ManyToOne // 다대일 관계 설정: 여러 계좌가 한 사용자를 가질 수 있음
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // User 엔티티와 관계 매핑

	@Column(name = "created_at", nullable = false)
	private Timestamp createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = new Timestamp(System.currentTimeMillis()); // 현재 시간을 설정
	}

	// 출금 기능
	public void withDraw(Long amount) {
		this.balance -= amount;
	}

	// 입금 기능
	public void deposit(Long amount) {
		this.balance += amount;
	}

	// 패스워드 체크
	public void checkPassword(String inputPassword) {
		if (!this.password.equals(inputPassword)) {
			throw new DataDeliveryException(Define.FAIL_ACCOUNT_PASSWROD, HttpStatus.BAD_REQUEST);
		}
	}

	// 잔액 여부 확인
	public void checkBalance(Long amount) {
		if ((this.balance - amount) < 0) {
			throw new DataDeliveryException(Define.LACK_Of_BALANCE, HttpStatus.BAD_REQUEST);
		}
	}

	// 계좌 소유자 확인 기능
	public void checkOwner(Integer principalId) {
		if (!this.user.getId().equals(principalId)) {
			throw new DataDeliveryException(Define.NOT_ACCOUNT_OWNER, HttpStatus.BAD_REQUEST);
		}
	}

	// 계좌 정보 존재 여부 확인
	public void checkAccount(Account dto) {
		if (dto == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
	}
}
