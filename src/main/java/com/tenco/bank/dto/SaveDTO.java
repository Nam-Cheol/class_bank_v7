package com.tenco.bank.dto;

import com.tenco.bank.repository.model.Account;

import com.tenco.bank.repository.model.User;
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
public class SaveDTO {

	private String number;
	private String password;
	private Long balance;
	
	public Account toAccount(User user) {
		return Account.builder()
						.number(this.number)
						.password(this.password)
						.balance(this.balance)
						.user(user)
						.build();
	}
	
}
