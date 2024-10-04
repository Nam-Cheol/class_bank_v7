package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import jakarta.persistence.*;
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
@Table(name = "history_tb") // JPA 테이블 매핑
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 및 자동 증가 설정
	private Integer id;

	@Column(nullable = false)
	private Long amount;

	@Column(name = "w_balance")
	private Long wBalance;

	@Column(name = "d_balance")
	private Long dBalance;

	@ManyToOne // 출금 계좌와 다대일 관계 설정
	@JoinColumn(name = "w_account_id")
	private Account wAccount;

	@ManyToOne // 입금 계좌와 다대일 관계 설정
	@JoinColumn(name = "d_account_id")
	private Account dAccount;

	@Column(name = "created_at", nullable = false)
	private Timestamp createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = new Timestamp(System.currentTimeMillis()); // 현재 시간을 설정
	}
}
