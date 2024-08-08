<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- header.jsp -->
<%@include file="/WEB-INF/view/layout/header.jsp"%>
<!-- start of content.jsp (xxx.jsp) -->
<div class="col-sm-8">

	<h2>계좌 이체(인증)</h2>
	<h5>Bank App에 오신 걸 환영합니다.</h5>

	<!-- 예외적으로 로그인은 보안때문에 post 방식으로 던지는 것이 좋다 -->
	<form action="/account/transfer" method="post">
		<div class="form-group">
			<label for="amount">출금 금액:</label>
			<input type="number" class="form-control" placeholder="Enter amount" id="amount" name="amount" value="1000">
		</div>
		<div class="form-group">
			<label for="wAccountNumber">출금 계좌 번호:</label>
			<input type="text" class="form-control" placeholder="Enter account number" id="wAccountNumber" name="wAccountNumber" value="1111">
		</div>
		<div class="form-group">
			<label for="dAccountNumber">입금 계좌 번호:</label>
			<input type="text" class="form-control" placeholder="Enter account number" id="dAccountNumber" name="dAccountNumber" value="2222">
		</div>
		<div class="form-group">
			<label for="dAccountNumber">비밀번호:</label>
			<input type="password" class="form-control" placeholder="Enter account number" id="password" name="password" value="1234">
		</div>
		<div class="text-right">
			<button type="submit" class="btn btn-primary">입금 요청</button>
		</div>
	</form>
	<!-- end of content.jsp (xxx.jsp) -->
</div>
</div>
<%@include file="/WEB-INF/view/layout/footer.jsp"%>
