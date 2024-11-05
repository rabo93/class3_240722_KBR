<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
	function logout(){
		//confirm() 함수 활용하여"로그아웃 하시겠습니까?" 질문을 표시하고
		// 확인 버튼 클릭 시 (true) "MemberLogout" 페이지로 이동 처리
		if(confirm("로그아웃 하시겠습니까?")) {
			location.href = "MemberLogout";
		}
	}
</script>

<div id="top_menu">
	<h4>
		<a href="./">HOME</a> 
		
		<c:choose>
			<c:when test="${empty sessionScope.sId }"> <%--로그인 상태가 아닌 경우(sId 없음) --%>
				| <a href="MemberLogin">로그인</a>
				| <a href="MemberJoin">회원가입</a>
			</c:when>
			<c:otherwise> <!-- 로그인 상태인 경우 -->
				| <a href="MemberInfo">${sessionScope.sId }</a>님
				| <a href="javascript:void(0)" onclick="logout()">로그아웃</a>
			</c:otherwise>
		
		</c:choose>
		
	</h4>
	<hr>
</div>

