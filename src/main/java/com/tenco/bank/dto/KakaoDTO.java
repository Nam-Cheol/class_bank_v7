package com.tenco.bank.dto;

import java.sql.Timestamp;

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
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoDTO {

	private Long id;
	private Timestamp connectedAt;
	private Properties properties;
	private KakaoAccount kakaoAccount;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
	public class Properties {
		private String nickname;
		private String profileImage;
		private String thumbnailImage;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
	public class KakaoAccount {
		private Boolean profileNicknameNeedsAgreement;
		private Boolean profileImageNeedsAgreement;
		private Profile profile;
		
		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
		public class Profile {
			private String nickname;
			private String thumbnailImageUrl;
			private String profileImageUrl;
		}
	}
	
}



