package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component // IoC의 대상(싱글톤)
public class AuthInterceptor implements HandlerInterceptor{

	/**
	 * preHandle 동작 흐름 
	 * 컨트롤러 들어오기 전에 동작함
	 * 단, 스프링부트 설정 파일, 설정 클래스에 등록이 돼야 함 >> 특정 URL
	 * 리턴값이 true일 때 컨트롤러 안으로 들여 보내고,
	 * false 일 경우 컨트롤러 안으로 들어갈 수 없다.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession();
		
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		
		if(principal == null) {
			throw new UnAuthorizedException("로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED);
		}
		
		return true;
	}
	
	/**
	 * postHandle
	 * 뷰가 렌더링 되기 바로 전에 콜백되는 메소드
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 * afterCompletion
	 * 요청 처리가 완료된 후, 뷰가 완전히 렌더링이 된 후에 호출된다.
	 * 
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
	
}
