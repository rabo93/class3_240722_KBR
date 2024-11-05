<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div id="footer_area">
	<hr>
	<!-- img태그를 사용하여 webapp/resources/images 디렉토리의 logo.png 파일 표시하기 -->
	<!-- 	<img src="/resources/images/logo.png"> -->
	
	<!-- 
		주의! 클라이언트가 실행하는 HTML 태그상에서 상대경로 루트(/) 지정시
		프로젝트 상의 루트가 아닌 서버상의 루트를 요청하므로 실제 요청 주소는 다음과 같다.
		http://localhost:8081/resources/images/logo.png 
	 	=> 현재 프로젝트 컨텍스트 루트가 test2이므로 이 경로 생략시 존재하지 않는 경로이므로 요청 오류 발생!
	 	=> 프로젝트 컨텍스트 루트를 실제 루트(/)로 설정시에는 아무런 문제가 발생하지 않는다!
	 -->
		
	<!-- 해결책1) /resources 앞에 컨텍스트루트 경로명을 명시: /test2/resources/images/logo.png -->
	<!-- 	<img src="/test2/resources/images/logo.png"> -->
	
	<!-- 해결책2) EL을 활용하여 현재 프로젝트의 컨텍스트 루트(ex. /test2)를 자동으로 입력 -->
	<img src="${pageContext.request.contextPath}/resources/images/logo.png">
	
</div>