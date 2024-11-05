<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 게시판</title>
<!-- 외부 CSS 파일(css/default.css) 연결하기 -->
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
<style type="text/css">
	#listForm {
		width: 1024px;
		max-height: 610px;
		margin: auto;
	}
	
	#listForm > table {
		margin: auto;
		width: 1024px;
	}
	
	#tr_top {
		background: orange;
		text-align: center;
	}
	
	table td {
		text-align: center;
	}
	
	/* 제목 열 좌측 정렬 및 여백 설정 */
	#listForm .board_subject {
		text-align: left;
		padding-left: 20px;
	}
	
	#listForm .board_subject:hover {
		background-color: #FFD8D8;
	}
	
	#pageList {
		margin: auto;
		width: 1024px;
		text-align: center;
	}
	
	#emptyArea {
		margin: auto;
		width: 1024px;
		text-align: center;
	}
	
	#commandArea {
		margin: auto;
		width: 1024px;
		text-align: right;
	}
	
	/* 하이퍼링크 밑줄 제거 */
	a {
		text-decoration: none;
	}
</style>
<script src="${pageContext.request.contextPath}/resources/js/jquery-3.7.1.js"></script>
</head>
<body>
	<header>
		<%-- inc/top.jsp 페이지 삽입(jsp:include 액션태그 사용 시 / 경로는 webapp 가리킴) --%>
		<jsp:include page="/WEB-INF/views/inc/top.jsp"></jsp:include>
	</header>
	<article>
		<!-- 게시판 리스트 -->
		<h1>게시판 글 목록</h1>
		<section id="commandArea">
			<%-- ====================== [ 게시물 검색 기능 ] ===================== --%>
			<%-- 검색을 위한 폼 생성 --%>
			<form action="BoardList" method="get">
				<%-- 검색타입 목록(셀렉트박스), 검색어(텍스트박스) 추가 --%>
				<%-- 파라미터에 해당 항목이 있을 경우 해당 내용 표시 --%>
				<select name="searchType">
					<option value="subject" <c:if test="${param.searchType eq 'subject'}">selected</c:if>>제목</option>
					<option value="content" <c:if test="${param.searchType eq 'content'}">selected</c:if>>내용</option>
					<option value="subject_content" <c:if test="${param.searchType eq 'subject_content'}">selected</c:if>>제목&내용</option>
					<option value="name" <c:if test="${param.searchType eq 'name'}">selected</c:if>>작성자</option>
				</select>
				<input type="text" name="searchKeyword" value="${param.searchKeyword}">
				<input type="submit" value="검색" />
				<input type="button" value="글쓰기" onclick="location.href='BoardWrite'" />
			</form>
		</section>
		<section id="listForm">
			<table>
				<tr id="tr_top">
					<td width="100px">번호</td>
					<td>제목</td>
					<td width="150px">작성자</td>
					<td width="150px">날짜</td>
					<td width="100px">조회수</td>
				</tr>
				<%-- =================================================================== --%>
				<%-- 페이지번호(pageNum 파라미터) 가져와서 저장(없을 경우 기본값 1 설정) --%>
				<%-- 1) 기본 pageNum 변수 설정(기본값 1로 설정) --%>
				<c:set var="pageNum" value="1" />
				<%-- 2) pageNum 파라미터가 존재할 경우 pageNum 변수에 해당 파라미터값 저장 --%>
				<c:if test="${not empty param.pageNum}">
					<c:set var="pageNum" value="${param.pageNum}" />
				</c:if>
				<%-- =================================================================== --%>
				<c:choose>
					<c:when test="${empty boardList}">
						<%-- 게시물 목록이 하나도 존재하지 않을 경우 --%>
						<tr><td colspan="5">게시물이 존재하지 않습니다.</td></tr>
					</c:when>
					<c:otherwise>
						<%-- 게시물 목록이 하나라도 존재할 경우 --%>
						<%-- JSTL 과 EL 사용하여 글목록 표시 작업 반복(boardList 객체) --%>
						<c:forEach var="board" items="#{boardList}">
							<%-- List 객체에서 꺼낸 BoardVO 객체(board)에 저장된 멤버변수값(데이터) 출력 --%>
							<tr>
								<td class="board_num">${board.board_num}</td>
								<td class="board_subject">${board.board_subject}</td>
								<td>${board.board_name}</td>
								<td>
									<%--
									JSTL - format(fmt) 라이브러리를 활용하여 날짜 및 시각 형식(포맷) 변경
									1) <fmt:formatDate> : Date 등의 날짜 및 포맷 객체의 날짜 형식 변경
									   => <fmt:formatDate value="${날짜 및 시각 객체}" pattern="표현패턴">
									   => 자바의 SimpleDateFormat 등의 포맷팅 클래스와 동일한 역할 수행
									2) <fmt:parseDate> : String 객체의 날짜 형식 변경
									----------------------------------------------------------------------
									[ 날짜 및 시각 형식을 지정하는 패턴 문자 ]
									y : 연도(yy : 연도 2자리, yyyy : 연도 4자리)
									M : 월(MM : 월 2자리)
									d : 일(dd : 일 2자리)
									H : 시(HH : 24시간제, hh : 12시간제)
									m : 분(mm : 분 2자리)
									s : 초(ss : 초 2자리) 
									--%>
									<fmt:formatDate value="${board.board_date}" pattern="yy-MM-dd HH:mm" />
								</td>
								<td>${board.board_readcount}</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</table>
		</section>
		<%-- =========================== 페이징 처리 영역 ======================== --%>
		<section id="pageList">
			<%-- 검색타입과 검색어(searchKeyword) 를 파라미터 결합 형태로 변수에 저장  --%>
			<c:set var="keywordParam" value="&searchType=${param.searchType}&searchKeyword=${param.searchKeyword}" />
						
			<%-- [이전목록] 버튼 클릭 시 페이지 목록의 이전 목록 요청(3 4 페이지일 경우 1 2 목록 요청) --%>
			<%-- 현재 목록의 시작페이지번호에서 페이지 번호 갯수를 뺀 페이지 요청 --%>
			<%-- 이 때, 검색어(searchKeyword) 파라미터가 존재할 경우 검색타입과 검색어도 URL 에 추가 --%>
			<%-- 시작 페이지가 1 페이지일 경우 비활성화(disabled) --%>
			<input type="button" value="&lt;&lt;" 
				onclick="location.href='BoardList?pageNum=${pageInfo.startPage - pageInfo.pageListLimit}<c:if test="${not empty param.searchKeyword}">${keywordParam}</c:if>'"
				<c:if test="${pageInfo.startPage eq 1}">disabled</c:if>
			>
			<%-- [이전] 버튼 클릭 시 이전 페이지 글목록 요청(파라미터로 현재 페이지번호 - 1 전달) --%>
			<%-- 현재 페이지가 1 페이지일 경우 비활성화(disabled) --%>
			<input type="button" value="이전" 
				onclick="location.href='BoardList?pageNum=${pageNum - 1}<c:if test="${not empty param.searchKeyword}">${keywordParam}</c:if>'"
				<c:if test="${pageNum eq 1}">disabled</c:if>
			>
			
			<%-- 계산된 페이지 번호가 저장된 PageInfo 객체(pageInfo)를 통해 페이지번호 출력 --%>
			<%-- startPage 부터 endPage 까지 1씩 증가하면서 페이지번호 표시 --%>
			<c:forEach var="i" begin="${pageInfo.startPage}" end="${pageInfo.endPage}">
				<%-- 각 페이지마다 하이퍼링크 설정(BoardList) => 페이지번호를 파라미터로 전달 --%>
				<%-- 단, 현재 페이지(i값과 pageNum 파라미터값이 동일)는 하이퍼링크 없이 굵게 표시 --%>
				<c:choose>
					<c:when test="${i eq pageNum}">
						<strong>${i}</strong>
					</c:when>
					<c:otherwise>
						<%-- 페이지번호 클릭 시 해당 페이지 번호를 파라미터로 전달 --%>
						<a href="BoardList?pageNum=${i}">${i}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
			<%-- [다음] 버튼 클릭 시 다음 페이지 글목록 요청(파라미터로 현재 페이지번호 + 1 전달)--%>
			<%-- 현재 페이지가 전체 페이지 수와 동일할 경우 비활성화(disabled) --%>
			<input type="button" value="다음" 
				onclick="location.href='BoardList?pageNum=${pageNum + 1}<c:if test="${not empty param.searchKeyword}">${keywordParam}</c:if>'"
				<c:if test="${pageNum eq pageInfo.maxPage}">disabled</c:if>
			>
			<%-- 현재 목록의 시작페이지번호에서 페이지 번호 갯수를 더한 페이지 요청 --%>
			<%-- 끝 페이지가 전체 페이지 수와 동일할 경우 비활성화(disabled) --%>
			<input type="button" value="&gt;&gt;" 
				onclick="location.href='BoardList?pageNum=${pageInfo.startPage + pageInfo.pageListLimit}<c:if test="${not empty param.searchKeyword}">${keywordParam}</c:if>'"
				<c:if test="${pageInfo.endPage eq pageInfo.maxPage}">disabled</c:if>
			>
		</section>
	</article>
	<script type="text/javascript">
		// 게시물 제목열 클릭 이벤트 핸들링
		$(".board_subject").on("click", function(event) {
// 			console.log(event.target);
			// 클릭 대상 요소의 부모 요소 탐색 => parent() 메서드 활용
			let parent = $(event.target).parent();
// 			console.log(parent.html()); // 부모요소 내의 HTML 태그 모두 출력됨
			
			// 부모 요소의 내부 요소 중에서 클래스 선택자 "board_num" 탐색 = find() 메서드 활용
			let board_num = $(parent).find(".board_num").text();
			console.log("board_num = " + board_num);
			
			// "BoardDetail" 서블릿 주소 요청 => 파라미터 : 글번호, 페이지번호
			// => 글번호는 위에서 탐색한 번호 사용
			// => 페이지번호는 c:set 태그로 설정한 변수값 그대로 사용
			location.href = "BoardDetail?board_num=" + board_num + "&pageNum=" + ${pageNum};
		});
	</script>
</body>
</html>













