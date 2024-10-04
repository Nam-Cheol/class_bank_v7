package com.tenco.bank.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.tenco.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.TransferDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.HistoryAccount;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;

@Controller // IoC 대상 (싱글톤으로 관리)
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

	// 계좌 생성 화면 요청 - DI 처리
	private final HttpSession session;
	private final AccountService service;
	private final UserService userService;

	/**
	 * 계좌 생성 페이지 요청 주소 설계 : http://localhost:8080/account/save
	 * 
	 * @return save.mustache
	 */
	@GetMapping("/save")
	public String savePage() {

		// 1. 인증 검사가 필요 (account 전체 필요함)
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		return "account/save";
	}

	@PostMapping("/save")
	public String saveProc(SaveDTO dto) {
		// 1. form 데이터 추출 (파싱 전략)
		// 2. 인증 검사
		// 3. 유효성 검사
		// 4. 서비스 호출
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		if (dto.getNumber() == null || dto.getNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		if (dto.getBalance() == null || dto.getBalance() <= 0) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		service.createAccount(dto, principal);

		return "redirect:/account/list";
	}

	/**
	 * 계좌 목록 화면 요청 주소 설계 : http://localhost:8080/account/list , ... /
	 * 
	 * @return
	 */
	@GetMapping("/list")
	public String listPage(Model model) {

		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		// 2. 유효성 검사

		// 3. 서비스 호출
		List<Account> accountList = userService.getUserAccounts(principal.getId());

		// JSP에 데이터를 넣어주는 방법
		model.addAttribute("accountList", accountList);
		model.addAttribute("isAccountList", !accountList.isEmpty());

		return "account/list";
	}

	/**
	 * 출금 페이지 요청
	 * 
	 * @return withdrawal.mustache
	 */
	@GetMapping("/withdrawal")
	public String withdrawalPage() {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		return "account/withdrawal";
	}

	@PostMapping("/withdrawal")
	public String withdrawalProc(WithdrawalDTO dto) {

		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		// 유효성 검사 (자바 코드를 개발) --> 스프링 부트 @Valid 라이브러리가 존재
		if (dto.getAmount() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}

		if (dto.getAmount().longValue() <= 0) {
			throw new DataDeliveryException(Define.W_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}

		if (dto.getWAccountNumber() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		if (dto.getWAccountPassword() == null || dto.getWAccountPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출
		service.updateAccountWithdraw(dto, principal.getId());

		return "redirect:/account/list";
	}

	// 입금 페이지 요청
	@GetMapping("/deposit")
	public String depositPage() {
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		return "account/deposit";
	}

	// 입금 처리 기능 만들기
	@PostMapping("/deposit")
	public String depositProc(DepositDTO dto) {

		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		if (dto.getAmount() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}

		if (dto.getAmount().longValue() < 0) {
			throw new DataDeliveryException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}

		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출
		service.updateAccountDeposit(dto, principal.getId());

		return "redirect:/account/list";
	}

	// 이체 페이지 요청
	@GetMapping("/transfer")
	public String transferPage() {
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		return "account/transfer";
	}

	// 이체 서비스 호출
	@PostMapping("/transfer")
	public String transferProc(TransferDTO dto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		
		if(dto.getAmount() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getAmount().longValue() < 0) {
			throw new DataDeliveryException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}

		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		service.updateAccountTransfer(dto, principal.getId());
		
		return "redirect:/account/list";
	}

	/**
	 * 계좌 상세 보기 페이지
	 * 주소 설계 : localhost:8080/account/detail/${1}?type=all, deposit, withdraw
	 * @return
	 */
	@GetMapping("/detail/{accountId}")
	public String detail(
			@PathVariable(name = "accountId") Integer accountId,
			@RequestParam(required = false, name = "type", defaultValue = "all") String type,
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@RequestParam(name = "size", defaultValue = "2") Integer size,
			Model model) {

		// 1. 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		// 2. 유효성 검사
		List<String> validType = Arrays.asList("all", "deposit", "withdrawal");

		if (!validType.contains(type)) {
			throw new DataDeliveryException("유효하지 않은 접근입니다.", HttpStatus.BAD_REQUEST);
		}

		// 페이지 개수를 계산하기 위해 총 페이지 수를 계산
		int totalRecords = service.countHistoryByAccountIdAndType(type, accountId);
		int totalPages = (int) Math.ceil((double) totalRecords / (double) size);

		// 이전 페이지와 다음 페이지 계산
		int previousPage = page > 1 ? page - 1 : 1;
		int nextPage = page < totalPages ? page + 1 : totalPages;

		// 페이지네이션 버튼 활성/비활성
		boolean ifPreviousDisabled = page == 1;
		boolean ifNextDisabled = page == totalPages;

		// 3. 서비스 호출
		Account account = service.readAccountById(accountId);
		List<HistoryAccount> historyList = service.readHistoryByAccountId(type, accountId, page, size);

		// 4. 모델에 속성 저장
		model.addAttribute("account", account);
		model.addAttribute("historyList", historyList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("previousPage", previousPage);
		model.addAttribute("nextPage", nextPage);
		model.addAttribute("ifPreviousDisabled", ifPreviousDisabled);
		model.addAttribute("ifNextDisabled", ifNextDisabled);
		model.addAttribute("type", type);
		model.addAttribute("size", size);

		// 페이지 번호 리스트 생성
		List<Integer> pages = IntStream.rangeClosed(1, totalPages)
				.boxed()
				.collect(Collectors.toList());
		model.addAttribute("pages", pages);

		return "account/detail";
	}


}
