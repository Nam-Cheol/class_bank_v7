package com.tenco.bank.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tenco.bank.dto.KakaoDTO;
import com.tenco.bank.dto.OAuthToken;
import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller // IoC의 대상 (싱글톤 패턴으로 관리됨)
@RequestMapping("/user") // 대문 처리
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private final UserService userService;
	private final HttpSession session;
	
	@Value("${tenco.key}")
	private String tencoKey;
	
	
	// 주소 설계 -> http://localhost:8080/user/sign-up
	/**
	 * 회원 가입 페이지 요청
	 * 주소 설계 http://localhost:8080/user/sign-up
	 * @return signUp.jsp
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		// prefix: /WEB-INF/view/
		// return: user/signUp
		// suffix: .jsp
		return "user/signUp";
	}
	
	/**
	 * 회원 가입 로직 처리 요청
	 * 주소 설계 http://localhost:8080/user/sign-up
	 * @param dto
	 * @return
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpDTO dto) {
		
		// controller에서 일반적인 코드 작업
		// 1. 인증검사 (여기서는 인증검사 불 필요)
		// 2. 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_FULLNAME, HttpStatus.BAD_REQUEST);
		}
		
		userService.createUser(dto);
		
		return "redirect:/user/sign-in";
	}

	/**
	 * 로그인 화면 요청
	 * 주소설계 : http://localhost:8080/user/sign-in
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		return "user/signIn";
	}
	
	/**
	 * 로그인 요청 처리
	 * 주소 설계 : http://localhost:8080/user/sign-in
	 * @return
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInDTO dto) {
		// 1. 인증 검사 x
		// 2. 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		// 서비스로 객체 전달
		User principal = userService.readUser(dto);
		
		// 세션 메모리에 등록 처리
		session.setAttribute(Define.PRINCIPAL, principal);
		// 새로운 페이지로 이동 처리
		// TODO - 계좌 목록 페이지 이동 처리 예정
		return "redirect:/account/list";
	}
	
	@GetMapping("/logout")
	public String logout() {
		session.invalidate(); // 로그아웃 됨
		return "redirect:/user/sign-in";
	}
	
	@GetMapping("/kakao")
//	@ResponseBody
	public String kakaoLogin(@RequestParam(name = "code", required = false) String code, @RequestParam(name = "error", required = false) String error) {
		
		if (code != null) {
			// 로그인이 성공한 상태
			URI uri = UriComponentsBuilder
					.fromHttpUrl("https://kauth.kakao.com/oauth/token")
					.build()
					.toUri();
			
			RestTemplate restT = new RestTemplate();
			
			HttpHeaders header1 = new HttpHeaders();
			header1.add("Content-type", "application/x-www-form-urlencoded; charset=utf-8");

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "authorization_code");
			params.add("client_id", "9c72754a52f84b89440dd568ef3b2507");
			params.add("redirect_uri", "http://localhost:8080/user/kakao");
			params.add("code", code);

			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, header1);

			ResponseEntity<OAuthToken> response = restT.exchange
								(uri, HttpMethod.POST, requestEntity, OAuthToken.class);
			
			System.out.println("카카오 토큰 : " + response.getBody().toString());
			
			HttpHeaders header2 = new HttpHeaders();
			header2.add("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
			header2.add("Authorization", "Bearer " + response.getBody().getAccessToken());
			HttpEntity<MultiValueMap<String, String>> kakaoInfo = new HttpEntity<>(null, header2);
			ResponseEntity<KakaoDTO> response2 = restT.exchange
					("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoInfo, KakaoDTO.class);
			
			
			// 최초 사용자라면 자동 회원가입 처리 (우리 서버)
			// 회원가입 이력이 있는 사용자라면 바로 세션 처리 (우리 서버)
			// 사전기반 --> 소셜 사용자는 비밀번호를 입력여부
			// 우리서버에 회원가입 시 password --> not null
			KakaoDTO dto = response2.getBody();
			System.out.println(tencoKey);
			SignUpDTO signUpDTO = SignUpDTO.builder()
								.username(dto.getProperties().getNickname())
								.fullname("OAuth_" + dto.getProperties().getNickname())
								.password(tencoKey)
								.build();
			// 2. 우리 사이트 최초 소셜 사용자인지 판별
			
			User oldUser = userService.searchUsername(signUpDTO.getUsername());
			if(oldUser == null) {
				// 사용자가 최초 소셜 로그인 사용자로 판별
				userService.createUser(signUpDTO);
				// 고민 !!
				oldUser.setUserName(dto.getProperties().getNickname());
				oldUser.setPassword(null);
				oldUser.setFullName("OAuth_" + dto.getProperties().getNickname());
				oldUser.setMFile(null);
				oldUser.setOriginFileName(dto.getProperties().getProfileImage());
			} else {
				oldUser.setSocialLogin(true);
				oldUser.setOriginFileName(dto.getProperties().getProfileImage());
			}
			
			// 자동 로그인 처리
			session.setAttribute(Define.PRINCIPAL, oldUser);
			
			return "redirect:/account/list";
		} else {
			if(error != null) {
				throw new RedirectException(error, HttpStatus.BAD_REQUEST);
			} else {
				throw new RedirectException(Define.UNKNOWN, HttpStatus.BAD_REQUEST);
			}
		}
		
	}
	
}
