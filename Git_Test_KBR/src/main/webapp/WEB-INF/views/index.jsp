<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Index</title>
<!-- 외부 css 파일(resources/css/default.css) 연결하기 -->
<!-- 외부 자원 접근을 위한 경로 지정 시 컨텍스트루트부터 탐색하지 않고 서버 상의 루트부터 탐색함 -->
<!-- 프로젝트명이 포함되는 상대경로로 지정하는 것이 안전하다! -->
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">

</head>
<body>
	<header>
		<!-- 기본 메뉴 표시 영역 - inc/top.jsp 페이지 삽입 -->
		<!-- 1) 상대경로를 사용하여 top.jsp 페이지를 지정할 경우 -->
		<!-- 	현재 index.jsp 파일 경로(views) 기준으로 inc/top.jsp 지정 -->
<%-- 		<jsp:include page="inc/top.jsp"></jsp:include> --%>
		<!-- 2) 루트(/) 기준으로 inc/top.jsp 지정 : /는 webapp 폴더 기준으로 이동함-->
		<jsp:include page="/WEB-INF/views/inc/top.jsp"></jsp:include>
		
	</header>
	<article>
		<h1>MVC 게시판</h1>
		<h3><a href="BoardWrite">글쓰기</a> </h3>
		<h3><a href="BoardList">글목록</a> </h3>
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
		
	</footer>
</body>
</html>