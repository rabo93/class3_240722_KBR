<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 게시판</title>
<!-- 외부 CSS 파일(css/default.css) 연결하기 -->
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
<style type="text/css">
	#writeForm {
		width: 500px;
		min-height: 550px;
		margin: auto;
		border: 1px solid gray
	}
	
	#writeForm table {
		margin: auto;
		width: 500px;
	}
	
	.write_td_left {
		width: 150px;
		text-align: center;
	}
	
	.write_td_right {
		width: 300px;
	}
	
	#board_name {
		background-color: #77777744;
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
	<article id="writeForm">
		<h1>게시판 글 등록</h1>
		<%-- 기본적으로 form 태그는 enctype 속성값이 "application/x-www-form-urlencoded" 값으로 설정됨(기본값) --%>
		<%-- 모든 폼 파라미터 데이터를 문자열 형식으로 인코딩하여 전송하는 형식 --%>
<!-- 	<form action="BoardWrite" name="writeForm" method="post" enctype="application/x-www-form-urlencoded"> -->
		<%-- 
		파일 업로드를 위해 enctype 속성값을 "multipart/form-data" 로 설정 필수! 
		=> 모든 파라미터를 인코딩 된 문자열이 아닌 multipart 라는 형식(파일 형식)으로 관리하므로
		   서버 측으로 업로드 파일이 실제 파일 형태로 정상적으로 전송되며 다른 파라미터도 전송됨
		=> 만약, 서버측 컨트롤러에서 파라미터 매핑 시 VO 클래스 등에 MultipartFile 타입 지정 시
		   enctype="multipart/form-data" 미 설정 시 응답코드 400번과 함께 서버측에서 예외 발생함
		   (WARN : org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver - Resolved [org.springframework.validation.BindException: org.springframework.validation.BeanPropertyBindingResult: 4 errors<EOL>Field error in object 'boardVO' on field 'file': rejected value []; codes [typeMismatch.boardVO.file,typeMismatch.file,typeMismatch.[Lorg.springframework.web.multipart.MultipartFile;,typeMismatch]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [boardVO.file,file]; arguments []; default message [file]]; default message [Failed to convert property value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile[]' for property 'file'; nested exception is java.lang.IllegalStateException: Cannot convert value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'file[0]': no matching editors or conversion strategy found]<EOL>Field error in object 'boardVO' on field 'file1': rejected value [rabbit.png]; codes [typeMismatch.boardVO.file1,typeMismatch.file1,typeMismatch.org.springframework.web.multipart.MultipartFile,typeMismatch]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [boardVO.file1,file1]; arguments []; default message [file1]]; default message [Failed to convert property value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'file1'; nested exception is java.lang.IllegalStateException: Cannot convert value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'file1': no matching editors or conversion strategy found]<EOL>Field error in object 'boardVO' on field 'file2': rejected value []; codes [typeMismatch.boardVO.file2,typeMismatch.file2,typeMismatch.org.springframework.web.multipart.MultipartFile,typeMismatch]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [boardVO.file2,file2]; arguments []; default message [file2]]; default message [Failed to convert property value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'file2'; nested exception is java.lang.IllegalStateException: Cannot convert value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'file2': no matching editors or conversion strategy found]<EOL>Field error in object 'boardVO' on field 'file3': rejected value []; codes [typeMismatch.boardVO.file3,typeMismatch.file3,typeMismatch.org.springframework.web.multipart.MultipartFile,typeMismatch]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [boardVO.file3,file3]; arguments []; default message [file3]]; default message [Failed to convert property value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'file3'; nested exception is java.lang.IllegalStateException: Cannot convert value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'file3': no matching editors or conversion strategy found]])
		--%>
		<form action="BoardWrite" name="writeForm" method="post" enctype="multipart/form-data">
			<table>
				<tr>
					<td class="write_td_left"><label for="board_name">글쓴이</label></td>
					<td class="write_td_right">
						<%-- 글쓴이(작성자)는 세션 아이디값을 그대로 사용하므로 그냥 출력(읽기 전용) --%>
						<input type="text" id="board_name" name="board_name" value="${sessionScope.sId}" readonly required />
					</td>
				</tr>
				<%-- 세션 아이디로 작성자를 구별하므로 비밀번호 불필요 --%>
<!-- 				<tr> -->
<!-- 					<td class="write_td_left"><label for="board_pass">비밀번호</label></td> -->
<!-- 					<td class="write_td_right"> -->
<!-- 						<input type="password" name="board_pass" required /> -->
<!-- 					</td> -->
<!-- 				</tr> -->
				<tr>
					<td class="write_td_left"><label for="board_subject">제목</label></td>
					<td class="write_td_right"><input type="text" id="board_subject" name="board_subject" required /></td>
				</tr>
				<tr>
					<td class="write_td_left"><label for="board_content">내용</label></td>
					<td class="write_td_right">
						<textarea id="board_content" name="board_content" rows="15" cols="40" required></textarea>
					</td>
				</tr>
				<tr>
					<td class="write_td_left"><label for="board_file">첨부파일</label></td>
					<td class="write_td_right">
						<%-- 첨부파일 기능은 input 태그의 type="file" 속성 활용 --%>
						<%-- 주의! 업로드 시 실제 파일을 서버로 전송하려면 form 태그 enctype 속성 필수! --%>
						<%-- 1) 한번에 하나의 파일(단일 파일) 선택 가능하게 할 경우 --%>
						<input type="file" name="file1">
						<input type="file" name="file2">
						<input type="file" name="file3">
						<hr>
						<%-- 2) 복수개의 파일(다중 파일) 선택 가능하게 할 경우 multiple 속성 추가 --%>
						<input type="file" name="file" multiple>
					</td>
				</tr>
			</table>
			<section id="commandCell">
				<input type="submit" value="등록">&nbsp;&nbsp;
				<input type="reset" value="다시쓰기">&nbsp;&nbsp;
				<input type="button" value="취소" onclick="history.back()">
			</section>
		</form>
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
	</footer>
</body>
</html>








