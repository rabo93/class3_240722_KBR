<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원탈퇴</title>
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
</head>
<body>
	<header>
		<jsp:include page="/WEB-INF/views/inc/top.jsp"></jsp:include>
	</header>
	<article>
		<h1>회원탈퇴</h1>
		<div id="loginForm">
			<h4>탈퇴 확인을 위해 비밀번호를 입력해주세요.</h4>
			<form action="MemberWithdraw" method="post">
				<input type="password" name="passwd" placeholder="패스워드" required><br><br>
				<input type="submit" value="회원탈퇴">
			</form>
		</div>	
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
	</footer>
</body>
</html>