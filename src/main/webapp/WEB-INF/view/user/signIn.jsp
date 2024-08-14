<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- header.jsp -->
<%@include file="/WEB-INF/view/layout/header.jsp"%>
<!-- start of content.jsp (xxx.jsp) -->
<div class="col-sm-8">

	<h2>로그인</h2>
	<h5>Bank App에 오신 걸 환영합니다.</h5>

	<!-- 예외적으로 로그인은 보안때문에 post 방식으로 던지는 것이 좋다 -->
	<form action="/user/sign-in" method="post">
		<div class="form-group">
			<label for="username">username:</label> <input type="text" class="form-control" placeholder="Enter username" id="username" name="username" value="길동">
		</div>
		<div class="form-group">
			<label for="pwd">Password:</label> <input type="password" class="form-control" placeholder="Enter password" id="pwd" name="password" value="1234">
		</div>
		<button type="submit" class="btn btn-primary">로그인</button>
		<div>
			<a href="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=9c72754a52f84b89440dd568ef3b2507&redirect_uri=http://localhost:8080/user/kakao" class="mt-4">
			<img alt="" src="/images/kakao_login_small.png">
			</a>
		</div>
	</form>
	<!-- end of content.jsp (xxx.jsp) -->
</div>
</div>
<%@include file="/WEB-INF/view/layout/footer.jsp"%>