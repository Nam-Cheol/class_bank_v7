package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import org.springframework.web.multipart.MultipartFile;

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
public class User {
	private Integer id;
	private String userName;
	private String password;
	private String fullName;
	private Timestamp createdAt;
	private MultipartFile mFile;
	private String originFileName;
	private String uploadFileName;
}
