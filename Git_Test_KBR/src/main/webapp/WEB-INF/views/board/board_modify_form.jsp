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
	#modifyForm {
		width: 500px;
		height: 100%;
		margin: auto;
	}
	
	#modifyForm table {
		border: 1px solid black;
		margin: auto;
		width: 450px;
	}
	
	.write_td_left {
		width: 100px;
		text-align: center;
	}
	
	.board_file {
		font-size: 12px;
		margin: 3px;
		vertical-align: middle;
	}
	
	.img_btn {
		width: 15px;
		height: 15px;
		vertical-align: middle;
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
	<article id="modifyForm">
		<h1>글 상세내용 보기</h1>
		<form action="BoardModify" method="post" enctype="multipart/form-data">
			<%-- 입력받지 않은 글번호, 페이지번호 파라미터를 hidden 속성으로 전달 --%>
			<input type="hidden" name="board_num" value="${param.board_num}">
			<input type="hidden" name="pageNum" value="${param.pageNum}">
			<table>
				<tr>
					<td class="write_td_left"><label for="board_name">작성자</label></td>
					<td class="write_td_right">
						<%-- 글쓴이(작성자)는 세션 아이디값을 그대로 사용하므로 그냥 출력(읽기 전용) --%>
						<input type="text" id="board_name" name="board_name" value="${sessionScope.sId}" readonly required />
					</td>
				</tr>
				<tr>
					<td class="write_td_left"><label for="board_subject">제목</label></td>
					<td class="write_td_right">
						<input type="text" id="board_subject" name="board_subject" value="${board.board_subject}" required />
					</td>
				</tr>
				<tr>
					<td class="write_td_left"><label for="board_content">내용</label></td>
					<td class="write_td_right">
						<textarea id="board_content" name="board_content" rows="15" cols="40" required>${board.board_content}</textarea>
					</td>
				</tr>
				<tr>
					<td class="write_td_left"><label for="board_file">첨부파일</label></td>
					<td class="write_td_right">
						<%--
						파일명 존재 여부 판별하여 업로드 버튼 or 파일명과 삭제버튼 표시
						forEach 태그 활용하여 fileList 객체 요소 갯수만큼 반복
						=> 반복 요소 인덱스값 또는 순서번호 확인을 위해 varStatus 속성 사용
						=> 반복 인덱스 : 속성명.index   반복 순서번호 : 속성명.count
						--%>
						<c:forEach var="file" items="${fileList}" varStatus="status">
							<div class="board_file">
								<%-- 파일명 존재할 경우 원본 파일명 표시하고 --%>
								<%-- 아니면, 파일 업로드 버튼 표시 --%>
								<c:choose>
									<c:when test="${not empty file}">
										<%-- 원본 파일명 출력(varStatus속성명.index 활용) --%>
										${originalFileList[status.index]}
										<%-- 파일 다운로드 링크(download-icon.png 이미지) 생성 --%>
										<a href="${pageContext.request.contextPath}/resources/upload/${file}" download="${originalFileList[status.index]}"><img src="${pageContext.request.contextPath}/resources/images/download-icon.png" class="img_btn" title="다운로드"></a>
										<%-- 파일 삭제 링크(delete-icon.png 이미지) 생성(개별 삭제 용도) --%>
										<%-- 하이퍼링크 클릭시 deleteFile() 함수 호출(파라미터 : 글번호, 실제 파일명, 카운트번호) --%>
										<a href="javascript:deleteFile(${board.board_num}, '${file}', ${status.count})"><img src="${pageContext.request.contextPath}/resources/images/delete-icon.png" class="img_btn" title="다운로드"></a>
										<%-- 만약, 파일명만 표시할 때 파일 업로드 요소도 파라미터로 전달하려면(파라미터값은 널스트링) --%>
										<%-- 파일 업로드 요소 추가 후 hidden 속성으로 숨김 처리 가능 --%>
										<input type="file" name="file${status.count}" hidden>
									</c:when>
									<c:otherwise>
										<%-- 파일 항목의 name 속성값을 다르게 부여하기 위해 file 문자열과 카운팅 속성값을 결합 --%>
										<%-- => forEach 태그의 status.count 활용(1부터 1씩 증가됨) => ex) "file" + 1 = "file1" --%>
										<input type="file" name="file${status.count}">
									</c:otherwise>
								</c:choose>
							</div>
						</c:forEach>
					</td>
				</tr>
			</table>
			<section id="commandCell">
				<input type="submit" value="수정">
				<input type="button" value="취소" onclick="history.back()">
			</section>
		</form>
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
	</footer>
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
		
		function deleteFile(board_num, file, index) {
			console.log(board_num +", "+ file + ", "+ index);
			
		}
	</script>
</body>
</html>








