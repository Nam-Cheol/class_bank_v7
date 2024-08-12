<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- header.jsp -->
<%@include file="/WEB-INF/view/layout/header.jsp"%>
<!-- start of content.jsp (xxx.jsp) -->
<div class="col-sm-8">

	<h2>계좌 목록(인증)</h2>
	<h5>Bank App에 오신 걸 환영합니다.</h5>

	<!-- 계좌가 없는 경우와 있는 경우를 분리 -->
	<!-- 계좌가 있는 사용자일 경우 반복문을 활용할 예정 -->
	<c:choose>
		<c:when test="${accountList != null}">
			<%-- 계좌 존재 : html 주석을 사용하면 오류발생 (jstl 태그 안에서) --%>
			<table class="table">
				<thead>
					<tr>
						<th>계좌 번호</th>
						<th>잔액</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="account" items="${accountList}">
						<tr>
							<td><a href="/account/detail/${account.id}?type=all">${account.number}</a></td>
							<td>${account.formatKoreaWon(account.balance)}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="d-flex justify-content-center">
			<ul class="pagination">
				<!-- Previous Page Link -->
				<li class="page-item <c:if test='${currentPage == 1}'>disabled</c:if>"><a class="page-link" href="?page=${currentPage - 1}&size=${size}">Previous</a></li>

				<!-- Page Numbers -->
				<!-- [Previous]  1 2 3 4 5 6 7 8   [Next] -->
				<c:forEach begin="1" end="${totalPages}" var="page">
					<li class="page-item  <c:if test='${page == currentPage}'>active </c:if>"><a class="page-link" href="?page=${page}&size=${size}">${page}</a></li>
				</c:forEach>

				<!-- Next Page Link  -->
				<li class="page-item <c:if test='${currentPage == totalPages}'>disabled</c:if>"><a class="page-link" href="?page=${currentPage + 1}&size=${size}">Next</a></li>
			</ul>
		</c:when>
		<c:otherwise>
			<div class="jumbotron display-4">
				<h5>아직 생성된 계좌가 없습니다.</h5>
			</div>
		</c:otherwise>
	</c:choose>
	
	<!-- end of content.jsp (xxx.jsp) -->
</div>
</div>
<%@include file="/WEB-INF/view/layout/footer.jsp"%>
