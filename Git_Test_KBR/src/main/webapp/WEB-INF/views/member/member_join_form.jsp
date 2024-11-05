<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/resources/js/jquery-3.7.1.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/member_join_form.js"></script>
</head>
<body>
	<header>
		<!-- 기본 메뉴 표시 영역 - inc/top.jsp 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/top.jsp"></jsp:include>
	</header>
	<article>
		<h1>회원가입</h1>
		<form action="MemberJoin" name="joinForm" method="post">
			<table border="1">
				<tr>
					<th>이름</th>
					<td><input type="text" name="name"></td>
				</tr>
				<tr>
					<th>ID</th>
					<td>
						<!-- ID중복확인 버튼 클릭 시 함수 호출하여 새 창으로 "check_id.jsp" 열기 -->
						<input type="text" name="id" id="id" placeholder="4 ~ 8글자 사이 입력" onblur="checkIdLength()">
						<input type="button" value="ID중복확인" onclick="checkId()">
						<div id="checkIdResult"></div>
					</td>
				</tr>
				<tr>
					<th>비밀번호</th>
					<td>
						<input type="password" id="passwd" name="passwd" placeholder="4 ~ 8글자 사이 입력">
						<div id="checkPasswdResult"></div>
					</td>
				</tr>
				<tr>
					<th>비밀번호확인</th>
					<td>
						<input type="password" id="passwd2">
						<div id="checkPasswd2Result"></div>
					</td>
				</tr>
				<tr>
					<th>주소</th>
					<td>
						<input type="text" id="postcode" name="post_code" size="6" readonly placeholder="우편번호">
						<input type="button" value="주소검색" onclick="search_address()"><br>
						<input type="text" id="address1" name="address1" size="25" readonly placeholder="기본주소"><br>
						<input type="text" id="address2" name="address2" size="25" placeholder="상세주소">
					</td>
				</tr>
				<tr>
					<th>E-Mail</th>
					<td>
						<input type="text" size="10" id="email1" name="email1">@<input type="text" size="10" id="email2" name="email2">
						<select id="emailDomain">
							<option value="">직접입력</option>
							<option value="naver.com">naver.com</option>
							<option value="nate.com">nate.com</option>
							<option value="gmail.com">gmail.com</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>직업</th>
					<td>
						<select name="job">
							<option value="">항목을 선택하세요</option>
							<option value="개발자">개발자</option>
							<option value="DB엔지니어">DB엔지니어</option>
							<option value="관리자">관리자</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>성별</th>
					<td>
						<input type="radio" name="gender" value="남">남
						<input type="radio" name="gender" value="여">여
					</td>
				</tr>
				<tr>
					<th>취미</th>
					<td>
						<input type="checkbox" id="hobby1" name="hobby" value="여행"><label for="hobby1">여행</label>
						<input type="checkbox" id="hobby2" name="hobby" value="독서"><label for="hobby2">독서</label>
						<input type="checkbox" id="hobby3" name="hobby" value="게임"><label for="hobby3">게임</label>
						<input type="checkbox" id="check_all"><label for="check_all">전체선택</label>
					</td>
				</tr>
				<tr>
					<th>가입동기</th>
					<td>
						<textarea rows="5" cols="40" id="motivation" name="motivation"></textarea>
					</td>
				</tr>
				<tr>
					<th>프로필 이미지</th>
					<td>
						<img src="${pageContext.request.contextPath}/resources/images/profile_default.png" id="preview_profile"><br>
						<input type="file" name="profile_img" id="profile_img">
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="submit" value="가입" id="btnSubmit">
						<input type="reset" value="초기화">
						<!-- 11. 돌아가기 버튼 클릭 시 이벤트 처리를 통해 이전 페이지로 이동 처리 -->
						<input type="button" value="돌아가기" onclick="history.back()">
					</td>
				</tr>
			</table>
		</form>
	</article>
	<footer>
		<!-- 회사 소개 영역(inc/bottom.jsp) 페이지 삽입 -->
		<jsp:include page="/WEB-INF/views/inc/bottom.jsp"></jsp:include>
	</footer>
	
	
	
	<!-- ==================================================================== -->
	<!-- 카카오(다음) 우편번호 검색 API 서비스 활용하여 주소 검색하기 -->
	<!-- 웹사이트 주소 : https://postcode.map.daum.net/guide -->
	<!-- 카카오(다음) 에서 제공하는 우편번호 검색 스크립트 파일 로딩 필수! -->
	<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
	<script type="text/javascript">
		function search_address() {
		    new daum.Postcode({ // postcode.v2.js 에서 제공하는 daum.Postcode 객체 생성
		    	// 주소검색 창에서 주소 검색 후 검색된 주소를 사용자가 클릭하면
		    	// oncomplete 이벤트에 의해 이벤트 뒤의 익명함수가 호출(실행)됨
		    	// => 개발자가 호출하는 것이 아니라 API 가 함수를 자동으로 호출하게 됨
		    	//    (= 어떤 동작 수행 후 자동으로 호출되는 함수를 콜백(callback) 함수라고 함)
		        oncomplete: function(data) {
		            // 클릭(선택)된 주소 정보가 익명함수 파라미터 data 에 객체 형태로 전달됨
		            console.log(data);
		            // => data.xxx 형식으로 각 주소 상세정보에 접근 가능
		            
		            // 1) 우편번호(= postcode 이나 최근 국가기초구역번호로 변경됨 = zonecode 사용)
		            document.joinForm.postcode.value = data.zonecode;
		    
		    		// 2) 기본주소(address 속성값)
// 		            document.joinForm.address1.value = data.address;
		    		
		    		// 만약, 해당 주소에 건물명(buildingName 속성값)이 존재할 경우(널스트링이 아님)
		    		// 기본주소 뒤에 건물명을 결합하여 출력
		    		// ex) 기본주소 : 부산광역시 부산진구 동천로 109
		    		//     건물명   : 삼한골든게이트
		    		//     => 부산광역시 부산진구 동천로 109 (삼한골든게이트)
		    		let address = data.address; // 기본주소 저장
		    		
		    		if(data.buildingName != "") { // 건물명 존재여부 판별
		    			address += " (" + data.buildingName + ")"; // 건물명 결합
		    		}
		    		
		    		// 기본주소 출력
		    		document.joinForm.address1.value = address;
		    		
		    		// 상세주소 입력 항목에 커서 요청
		    		document.joinForm.address2.focus(); 
		    		
		        }
		    }).open(); // 주소검색창 표시(새 창 열기)
		}
	</script>
</body>
</html>









