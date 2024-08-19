package com.tenco.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
// JSON 형식의 코딩 컨벤션인 스네이크 케이스를 카멜 노테이션으로 할당
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OAuthToken {
	
	private String accessToken;
	private String tokenType;
	private Integer expiresIn;
	private String refreshToken;
	private Integer refreshTokenExpiresIn;
	private String scope;
	
}
