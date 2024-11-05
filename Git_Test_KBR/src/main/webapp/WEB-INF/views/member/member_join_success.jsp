<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>가입완료</title>
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
</head>
<body>
	<header>
		<jsp:include page="/WEB-INF/views/inc/top.jsp"></jsp:include>
	</header>
	<article>
		<h1>회원 가입 완료</h1>
		<table>
			<tr>
				<th>
					인증메일이 발송되었습니다.<br>
					이메일 인증 수행 후 로그인이 가능합니다.<br>
				</th>
			</tr>
			<tr>
				<th>
					<input type="button" value="홈으로" onclick="location.href='./'">
					<input type="button" value="로그인" onclick="location.href='MemberLogin'">
				</th>
			</tr>
		</table>
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
	</footer>
</body>
</html>










