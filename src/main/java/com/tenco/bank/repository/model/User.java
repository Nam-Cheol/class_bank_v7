package com.tenco.bank.repository.model;

import java.sql.Timestamp;
import java.util.List;

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
@Entity // JPA 엔티티로 지정
@Table(name = "user_tb") // 매핑할 테이블 이름 지정
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "username",nullable = false, unique = true, length = 50)
	private String userName;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(name = "fullname", nullable = false, length = 50)
	private String fullName;

	@Column(name = "created_at", nullable = false)
	private Timestamp createdAt;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private List<Account> accounts; // User가 가진 여러 계좌

	@PrePersist
	protected void onCreate() {
		this.createdAt = new Timestamp(System.currentTimeMillis());
	}
}
