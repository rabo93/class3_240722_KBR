<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 게시판</title>
<!-- 외부 CSS 파일(css/default.css) 연결하기 -->
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
<style type="text/css">
	#articleForm {
		width: 500px;
		height: 100%;
		margin: auto;
	}
	
	#articleForm table {
		border: 1px solid black;
		margin: auto;
		width: 500px;
	}
	
	th, td {
 		border: 1px solid black;
	}
	
	th {
		width: 70px;
	}
	
	td {
		text-align: center;
	}
	
	#basicInfoArea {
		height: auto;
		text-align: center;
	}
	
	#board_file {
		height: auto;
		font-size: 12px;
	}
	
	#board_file div {
		margin: 3px;
	}
	
	#articleContentArea {
		background: orange;
		margin-top: 10px;
		min-height: 200px;
		text-align: center;
		overflow: auto;
		white-space: pre-line;
	}
	
	#commandCell {
		text-align: center;
		margin-top: 10px;
		padding: 10px;
		border-top: 1px solid gray;
	}
</style>
</head>
<body>
	<header>
		<%-- inc/top.jsp 페이지 삽입(jsp:include 액션태그 사용 시 / 경로는 webapp 가리킴) --%>
		<jsp:include page="/WEB-INF/views/inc/top.jsp"></jsp:include>
	</header>
	<!-- 게시판 등록 -->
	<article id="articleForm">
		<h1>글 상세내용 보기</h1>
		<section id="basicInfoArea">
			<table>
				<tr>
					<th class="td_title">제목</th>
					<td colspan="3">${board.board_subject}</td>
				</tr>
				<tr>
					<th class="td_title">작성자</th>
					<td>${board.board_name}</td>
					<th class="td_title">작성일시</th>
					<td><fmt:formatDate value="${board.board_date}" pattern="yy-MM-dd HH:mm" /></td>
				</tr>
				<tr>
					<th class="td_title">작성자IP</th>
					<td>${board.board_writer_ip}</td>
					<th class="td_title">조회수</th>
					<td>${board.board_readcount}</td>
				</tr>
				<tr>
					<th class="td_title">첨부파일</th>
					<td colspan="3" id="board_file">
<!-- 						<div> -->
							<%--
							파일 다운로드 기능을 추가하려면 하이퍼링크에 download 속성 설정 추가 시 다운로드 가능
							=> 만약, 버튼으로 파일 다운로드를 수행하려면 버튼을 하이퍼링크로 감싸기
							=> download 속성에 속성값 지정 시 지정된 이름으로 다운로드 파일명 변경 가능
							--%>
<%-- 							<a href="${pageContext.request.contextPath}/resources/upload/${board.board_file1}" download>${board.board_file1}</a> --%>
<%-- 							${board.board_file1} --%>
<%-- 							<a href="${pageContext.request.contextPath}/resources/upload/${board.board_file1}" download}"> --%>
<!-- 								<input type="button" value="다운로드"> -->
<!-- 							</a> -->
							<%-- ============================================================== --%>
							<%-- [ JSTL - functions 라이브러리 함수 활용하여 원본 파일명 추출 ] --%>
							<%-- 1) split() 함수 활용하여 "-" 구분자로 분리 후 두번째 인덱스값 사용 --%>
							<%--    단, _ 기호가 2개 이상일 경우 반복문을 통해 문자열 결합 추가 필요 --%>
<%-- 							split() : ${fn:split(board.board_file1, "_")[1]} --%>

							<%-- 2) substring() 함수 활용하여 시작인덱스부터 지정인덱스까지 추출 --%>
							<%-- 2-1) 파일명의 길이 알아내기 --%>
<%-- 							<c:set var="fileLength" value="${fn:length(board.board_file1)}" /> --%>
							<%-- 2-2) 구분자(_)의 인덱스 알아내기(복수개일 경우 첫번째 기호 탐색) --%>
<%-- 							<c:set var="delimIndex" value="${fn:indexOf(board.board_file1, '_')}" /> --%>
							<%-- 2-3) substring() 함수 활용하여 추출(파일명과 구분자 인덱스 활용) --%>
							<%--      => substring(원본문자열, 시작인덱스, 끝인덱스) --%>
<%-- 							substring() : ${fn:substring(board.board_file1, delimIndex + 1, fileLength)} --%>

							<%-- 3) substringAfter() 함수 활용하여 지정된 문자 다음부터 끝까지 추출 --%>
<%-- 							substringAfter() : ${fn:substringAfter(board.board_file1, '_')} --%>
<%-- 							${board.board_file1} --%>
<%-- 							<a href="${pageContext.request.contextPath}/resources/upload/${board.board_file1}" download="${fn:substringAfter(board.board_file1, '_')}"> --%>
<!-- 								<input type="button" value="다운로드"> -->
<!-- 							</a> -->
<!-- 						</div> -->
<%-- 						<div>${board.board_file2}</div> --%>
<%-- 						<div>${board.board_file3}</div> --%>
						<%-- ============================================================= --%>
						<%-- 컨트롤러에서 파일명을 추출하여 전달한 경우 --%>
						<%-- List 객체("fileList") 크기만큼 반복문을 통해 파일명 출력 --%>
<%-- 						<c:forEach var="i" begin="0" end="${fileList.size() - 1}"> --%>
<%-- 							${fileList[i]} : ${originalFileList[i]}<br> --%>
<%-- 						</c:forEach> --%>
						<%-- 향상된 for문(foreach) 사용 시 --%>
						<%-- 기본 반복 객체 지정 후 다른 객체는 varStatus 속성을 통해 index 값 사용 가능 --%>
						<c:forEach var="file" items="${fileList}" varStatus="status">
							<%-- 단, 파일명이 존재할 경우에만 출력 --%>
							<c:if test="${not empty file}">
								<div>
									${file}
									<a href="${pageContext.request.contextPath}/resources/upload/${file}" download="${originalFileList[status.index]}">
										<input type="button" value="다운로드">
									</a>
								</div>
							</c:if>
						</c:forEach>
					</td>
				</tr>
			</table>
		</section>
		<section id="articleContentArea">
			${board.board_content}
		</section>
		<section id="commandCell">
			<%-- 답글, 수정, 삭제 버튼 모두 로그인 한 사용자에게만 표시 --%>
			<c:if test="${not empty sessionScope.sId}">
				<input type="button" value="답글작성" onclick="">&nbsp;&nbsp;
				<%-- 수정, 삭제 버튼은 작성자가 세션 아이디와 동일할 경우에만 표시 --%>
				<%-- 단, 관리자("admin") 의 경우 다른 작성자의 게시물에도 수정, 삭제 버튼 표시 --%>
				<c:if test="${sessionScope.sId eq board.board_name or sessionScope.sId eq 'admin'}">
					<input type="button" value="수정" onclick="requestModify()">&nbsp;&nbsp;
					<input type="button" value="삭제" onclick="confirmDelete()">&nbsp;&nbsp;
				</c:if>
			</c:if>
			
			<%-- 목록 버튼은 항상 표시하고, 이전 페이지로 돌아가기로 처리 --%>
			<input type="button" value="목록" onclick="history.back()">
		</section>
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
	</footer>
	
	<!-- ----------------------------------------------------------------------------------------- -->
	<script src="${pageContext.request.contextPath}/resources/js/jquery-3.7.1.js"></script>
	<script type="text/javascript">
		function getQueryParams() {
			let params = "";
			
			// URL 에서 파라미터 탐색하여 파라미터가 존재하면 URL 뒤에 파라미터 결합
			let searchParams = new URLSearchParams(location.search);
			for(let param of searchParams) {
				params += param[0] + "=" + param[1] + "&";
			}
			
			// 마지막 파라미터 뒤에 붙은 & 기호 제거
			if(params.lastIndexOf("&") == params.length - 1) { // & 기호가 배열의 끝에 있을 경우
				// & 기호 앞까지 추출하여 url 변수에 저장(덮어쓰기)
				params = params.substring(0, params.length - 1);
			}
			
			// 파라미터 결합된 문자열 리턴
			return params;
		}
	
		function confirmDelete() {
			if(confirm("삭제하시겠습니까?")) {
				location.href = "BoardDelete?" + getQueryParams(); // 페이지 요청
			}
		}
		
		function requestModify() {
				location.href = "BoardModify?" + getQueryParams(); // 페이지 요청
		}
	</script>


</body>
</html>








