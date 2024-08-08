package com.tenco.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;

import jakarta.servlet.http.HttpSession;

@Controller // IoC 대상 (싱글톤으로 관리)
@RequestMapping("/account")
public class AccountController {

	// 계좌 생성 화면 요청 - DI 처리
	private final HttpSession session;
	private final AccountService service;
	
	@Autowired
	public AccountController(HttpSession session, AccountService service) {
		this.session = session;
		this.service = service;
	}
	
	/**
	 * 계좌 생성 페이지 요청
	 * 주소 설계 : http://localhost:8080/account/save
	 * @return save.jsp
	 */
	@GetMapping("/save")
	public String savePage() {
		
		// 1. 인증 검사가 필요 (account 전체 필요함)
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다", HttpStatus.UNAUTHORIZED);
		}
		
		return "account/save";
	}
	
	@PostMapping("/save")
	public String saveProc(SaveDTO dto) {
		// 1. form 데이터 추출 (파싱 전략)
		// 2. 인증 검사
		// 3. 유효성 검사
		// 4. 서비스 호출
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다", HttpStatus.UNAUTHORIZED);
		}
		
		if(dto.getNumber() == null || dto.getNumber().trim().isEmpty()) {
			throw new DataDeliveryException("계좌번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException("비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getBalance() == null || dto.getBalance() <= 0) {
			throw new DataDeliveryException("계좌잔액을 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		service.createAccount(dto, principal.getId());
		
		// TODO - 계좌 리스트로 이동 예정
		return "redirect:/index";
	}
	
	/**
	 * 계좌 목록 화면 요청
	 * 주소 설계 : http://localhost:8080/account/list , ... /
	 * @return
	 */
	@GetMapping({"/list", "/"})
	public String listPage(Model model) {
		
		// 1. 인증검사
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}
		
		// 2. 유효성 검사
		
		// 3. 서비스 호출
		List<Account> accountList = service.readAccountListByUserId(principal.getId());
		
		// JSP에 데이터를 넣어주는 방법
		if(accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}
		
		return "account/list";
	}
	
	@PostMapping({"/list", "/"})
	public String listProc() {
		
		return "redirect:/account/list";
	}
}
