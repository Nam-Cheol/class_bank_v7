package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

import lombok.RequiredArgsConstructor;

// @Component >> 하나의 클래스를 IoC하고 싶다면 사용
@Configuration // >> 여러 개의 Bean으로 등록 가능
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired // DI
	private final AuthInterceptor authInterceptor;

//	@RequiredArgsConstructor <-- 생성자 대신 사용 가능
	
	// 우리가 만든 AuthInterceptor를 등록해야 함.
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/account/**")
				.addPathPatterns("/auth/**");
	}
	
	// 코드 추가
	// 서버 컴퓨터 상에 실제 이미지 경로지만 (C:\\work_spring\\uploads)
	// 프로젝트 상에서 (클라이언트가 HTML 소스로 보이는 경로는) /images/uploads/** 로 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry.addResourceHandler("/images/uploads/**")
				.addResourceLocations("file:\\C:\\work_spring\\uploads/");
		
	}
	
	@Bean // IoC 대상 (싱글톤 처리)
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
