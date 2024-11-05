<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
</head>
<body>
	<header>
		<jsp:include page="/WEB-INF/views/inc/top.jsp"></jsp:include>
	</header>
	<article>
		<h1>로그인</h1>
		<div id="loginForm">
			<form action="MemberLogin" method="post">
				<input type="text" name="id" placeholder="아이디" required><br>
				<input type="password" name="passwd" placeholder="패스워드" required><br>
				<%-- checkbox 생성 시 value 속성 지정하지 않으면, 체크 시 "on", 미 체크 시 null --%>
				<input type="checkbox" name="rememberId">아이디 기억하기<br>
				<input type="submit" value="로그인">
			</form>
		</div>	
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
	</footer>
</body>
</html>