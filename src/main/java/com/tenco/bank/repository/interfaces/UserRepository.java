package com.tenco.bank.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tenco.bank.repository.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	// username과 password로 사용자 조회 (로그인 기능)
	@Query("SELECT u FROM User u WHERE u.userName = :username AND u.password = :password")
	User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

	// 필요시 추가적인 커스텀 메서드 작성 가능
}
