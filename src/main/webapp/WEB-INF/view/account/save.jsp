<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- header.jsp -->
<%@include file="/WEB-INF/view/layout/header.jsp"%>
<!-- start of content.jsp (xxx.jsp) -->
<div class="col-sm-8">

	<h2>계좌생성(인증)</h2>
	<h5>Bank App에 오신 걸 환영합니다.</h5>

	<!-- 예외적으로 로그인은 보안때문에 post 방식으로 던지는 것이 좋다 -->
	<form action="/account/save" method="post">
		<div class="form-group">
			<label for="number">Number:</label>
			<input type="text" class="form-control" placeholder="Enter username" id="number" name="number" value="1002-1234">
		</div>
		<div class="form-group">
			<label for="pwd">Password:</label>
			<input type="password" class="form-control" placeholder="Enter password" id="pwd" name="password" value="1234">
		</div>
		<div class="form-group">
			<label for="balance">Balance:</label>
			<input type="number" class="form-control" placeholder="Enter balance" id="balance" name="balance" value="10000">
		</div>
		<div class="text-right">
			<button type="submit" class="btn btn-primary">계좌 생성</button>
		</div>
	</form>
	<!-- end of content.jsp (xxx.jsp) -->
</div>
</div>
<%@include file="/WEB-INF/view/layout/footer.jsp"%>
