package com.tenco.bank.dto;

import org.springframework.web.multipart.MultipartFile;

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
public class SignUpDTO {

	private String username;
	private String password;
	private String fullname;
	private MultipartFile mFile;
	private String originFileName;
	private String uploadFileName;

	// 2단계 로직 - User Object 반환
	public User toUser() {
		return User.builder()
				.userName(this.getUsername())
				.password(this.getPassword())
				.fullName(this.getFullname())
				.originFileName(this.getOriginFileName())
				.uploadFileName(this.getUploadFileName())
				.build();
	}

}
