<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- header.jsp -->
<%@include file="/WEB-INF/view/layout/header.jsp"%>
<!-- start of content.jsp (xxx.jsp) -->
<div class="col-sm-8">

	<h2>계좌 상세 보기(인증)</h2>
	<h5>Bank App에 오신 걸 환영합니다.</h5>

	<div class="bg-light p-md-5 p-75">
		<div class="user--box">
			${principal.userName}님 계좌 <br> 계좌번호 : ${account.number}<br> 잔액 : ${account.formatKoreaWon(account.balance)}
		</div>
		<br>
		<div>
			<a href="/account/detail/${account.id}?type=all&page=1" class="btn btn-outline-primary">전체</a>&nbsp; <a href="/account/detail/${account.id}?type=deposit&page=1"
				class="btn btn-outline-primary">입금</a>&nbsp; <a href="/account/detail/${account.id}?type=withdrawal&page=1" class="btn btn-outline-primary">출금</a>&nbsp;
		</div>
		<br>

		<!-- 테이블 부분 -->
		<div id="historyTable">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>날짜</th>
						<th>보낸 이</th>
						<th>받은 이</th>
						<th>입출금 금액</th>
						<th>계좌 잔액</th>
					</tr>
				</thead>
				<tbody id="historyBody">
					<c:forEach var="historyAccount" items="${historyList}">
						<tr>
							<th>${historyAccount.timestampToString(historyAccount.createdAt)}</th>
							<th>${historyAccount.sender}</th>
							<th>${historyAccount.receiver}</th>
							<th>${historyAccount.formatKoreaWon(historyAccount.amount)}</th>
							<th>${historyAccount.formatKoreaWon(historyAccount.balance)}</th>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!-- 페이지네이션 -->
		<div id="pagination">
			<c:forEach var="i" begin="1" end="${totalSize}">
				<div class="btn btn-outline-primary">
					<a href="#" onclick="fetchPage(${i}); return false;">${i}</a>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<script>
    function fetchPage(page) {
        const accountId = ${account.id};
        const type = ${type}; // 필요한 경우 'deposit'이나 'withdrawal'로 변경
        const url = `/account/detailNext/${accountId}?type=${type}&page=${page}`;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                updateTable(data.historyList);
            })
            .catch(error => console.error('Error:', error));
    }

    function updateTable(historyList) {
        const tableBody = document.getElementById('historyBody');
        tableBody.innerHTML = ''; // 기존 내용을 비우기

        historyList.forEach(historyAccount => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <th>${historyAccount.timestampToString(historyAccount.createdAt)}</th>
                <th>${historyAccount.sender}</th>
                <th>${historyAccount.receiver}</th>
                <th>${historyAccount.formatKoreaWon(historyAccount.amount)}</th>
                <th>${historyAccount.formatKoreaWon(historyAccount.balance)}</th>
            `;
            tableBody.appendChild(row);
        });
    }
</script>

<!-- end of content.jsp (xxx.jsp) -->
</div>
</div>
<%@include file="/WEB-INF/view/layout/footer.jsp"%>
