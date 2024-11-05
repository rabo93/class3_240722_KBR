package com.itwillbs.gittest_kbr.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itwillbs.gittest_kbr.service.BoardService;
import com.itwillbs.gittest_kbr.vo.BoardVO;
import com.itwillbs.gittest_kbr.vo.PageInfo;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	// 이클립스 상의 가상의 업로드 경로명 저장(프로젝트 상에서 보이는 경로)
	private String uploadPath = "/resources/upload";
	
	// =========================================================================================
	// [ 글쓰기 폼("BoardWrite" - GET) ]
	@GetMapping("BoardWrite")
	public String boardWriteForm(HttpSession session, Model model, HttpServletRequest request) {
		// 세션 아이디 체크하여 세션 아이디가 없을 경우 "result/fail" 페이지 포워딩 처리
		// => "msg" 속성값 : "로그인 필수!"
		// => "targetURL" 속성값 : "MemberLogin" (로그인페이지 URL)
		String id = (String)session.getAttribute("sId");
		if(id == null) {
			model.addAttribute("msg", "로그인 필수!\\n로그인 페이지로 이동합니다.");
			model.addAttribute("targetURL", "MemberLogin");
			
			// 로그인 성공 후 다시 현재 페이지로 돌아오기 위해 prevURL 세션 속성값 설정
			// => 경로를 직접 입력하지 않고 request 객체의 getServletPath() 메서드로 서블릿 주소 추출 가능
			String prevURL = request.getServletPath();
			String queryString = request.getQueryString(); // URL 파라미터 가져오기(없으면 null)
//			System.out.println("prevURL : " + prevURL);
//			System.out.println("queryString : " + queryString);
			
			// URL 파라미터(쿼리)가 null 이 아닐 경우 prevURL 에 결합(? 포함)
			if(queryString != null) {
				prevURL += "?" + queryString;
			}
			
			// 세션 객체에 prevURL 값 저장
			session.setAttribute("prevURL", prevURL);
			
			return "result/fail";
		}
		// -------------------------------------------------------------------------
		return "board/board_write_form";
	}
	
	// ---------------------------------------------------------------------
	// [ 글쓰기 비즈니스 로직("BoardWrite" - POST) ]
	// => 글쓰기 폼의 form 태그에 enctype="multipart/form-data" 속성 추가 필수!
	// => 또한, servlet-context.xml 파일에서 MultipartResolver 객체 설정 필수!
	@PostMapping("BoardWrite")
	public String boardWrite(BoardVO board, HttpServletRequest request, HttpSession session, Model model) {
		System.out.println(board);
		/*
		 * board_file=null, board_file1=null, board_file2=null, board_file3=null
		 * => VO 에서 파일명 저장하는 String 타입 변수에 해당하는 파라미터는 존재하지 않으므로 null
		 * 
		 * file=[MultipartFile[field="file", filename=, contentType=application/octet-stream, size=0]]
		 * => 다중 파일 업로드 시 MultipartFile[] 타입 배열 형태로 관리됨
		 *
		 * file1=MultipartFile[field="file1", filename=rabbit.png, contentType=image/png, size=33383] 
		 * file2=MultipartFile[field="file2", filename=3.jpg, contentType=image/jpeg, size=113749]
		 * file3=MultipartFile[field="file3", filename=, contentType=application/octet-stream, size=0]
		 * => 단일 파일 업로드 시 MultipartFile 타입 객체로 관리됨(단일 파일 업로드 가능 갯수 3개)
		 */
		
		// -------------------------------------------------------------------------------
		// 작성자 IP 주소 정보 가져와서 BoardVO 객체에 저장 - HttpServletRequest 객체 필요
		board.setBoard_writer_ip(request.getRemoteAddr());
		// 임시) localhost 로 접속 시 자신의 IP 주소가 IPv6 형태(0:0:0:0:0:0:0:1)로 표시되므로
		//       IPv4 형태(127.0.0.1)로 변환하여 저장
		if(board.getBoard_writer_ip().equals("0:0:0:0:0:0:0:1")) {
			board.setBoard_writer_ip("127.0.0.1");
		}
//		System.out.println("작성자 IP 주소 : " + board.getBoard_writer_ip());
		// ======================================================================================
		// [ 파일 업로드 처리 ]
		// 실제 파일 업로드 처리를 수행하기 위해 프로젝트 상의 가상의 업로드 경로 생성 필요(upload)
		// => 외부에서 업로드 파일에 접근(다운로드) 가능하도록 resources 경로에 생성
		// => 단, 실제 파일이 업로드 되는 위치는 별도의 경로로 관리됨(동일한 이름으로 생성됨)
		// => 가상의 경로 예시 : D:\Shared\Backend\Spring\workspace_spring3\Spring_MVC_Board\src\main\webapp\resources\ upload
		// => 실제 경로 예시 : D:\Shared\Backend\Spring\workspace_spring3\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Spring_MVC_Board\resources\ upload
		//    (경로 주석에서 경로 마지막 부분의 \ 와 u 는 유니코드 이스케이프 문자로 인식되어 공백 하나 추가함)
		// -------------------------------------------------------------------------------
//		String uploadPath = "/resources/upload"; // 가상의 경로명 저장(이클립스 프로젝트상의 경로)
		// => 다른 메서드에서도 재사용 가능하도록 멤버변수로 선언
		
		// 가상 경로에 대한 서버상의 실제 경로(톰캣이 관리하는 실제 경로) 알아내기
		// => 이클립스 프로젝트 상에서 업로드 폴더 생성 후 파일 업로드 수행 시
		//    이클립스에 연결된 톰캣이 관리하는 폴더에 업로드 폴더가 생성되기 때문
		//    (외부 톰캣 사용시에도 해당 톰캣 디렉토리 내에 업로드 폴더가 생성됨)
		// => request 객체 또는 session 객체의 getServletContext().getRealPath() 메서드 활용
		//    (파라미터 : 가상의 업로드 경로명)
//		String realPath = request.getServletContext().getRealPath(uploadPath);
		String realPath = session.getServletContext().getRealPath(uploadPath);
//		System.out.println("실제 업로드 경로 : " + realPath);
		// 실제 업로드 경로 : D:\Shared\Backend\Spring\workspace_spring3\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Spring_MVC_Board\resources\ upload
		// -------------------------------------------------------------------------------
		// [ 경로 관리 ]
		// 업로드 파일에 대한 관리 용이성을 증대시키기 위해 서브(하위) 디렉토리 활용하여 분산 관리
		// => 날짜별로 하위 디렉토리를 분류
		String subDir = ""; // 서브 디렉토리명을 저장할 변수 선언
		
		// 파일 업로드 시점에 맞는 날짜별 서브디렉토리 생성
		// => java.util.Date 또는 java.time.LocalXXX 클래스 활용(LocalXXX 클래스가 더 효율적)
		// 1. 현재 시스템의 날짜 정보를 갖는 객체 생성
		// 1-1) java.util.Date 클래스 활용
//		Date now = new Date(); // 기본 생성자 호출 시 시스템(톰캣)의 현재 날짜 및 시각 정보 생성
//		System.out.println(now); // Tue Oct 29 11:37:27 KST 2024
		
		// 1-2) java.time.LocalXXX 클래스 활용
		// => 날짜 정보만 관리할 경우 LocalDate, 시각 정보는 LocalTime, 날짜 및 시각 정보는 LocalDateTime 클래스 활용
		LocalDate today = LocalDate.now(); // 현재 시스템의 날짜 정보 생성
//		System.out.println(today); // 2024-10-29
		// -----------------------
		// 2. 날짜 포맷을 디렉토리 형식에 맞게 변경(ex. 2024-10-29 => 2024/10/29)
		// => 단, 윈도우 운영체제 기준으로 디렉토리 구분자는 백슬래시(\)로 표기하지만
		//    자바 또는 자바스크립트 문자열로 지정할 때 이스케이스 문자로 취급되므로
		//    백슬래시 2번(\\) 또는 슬래시(/) 기호로 경로 구분자 사용
		String datePattern = "yyyy/MM/dd"; // 날짜 포맷 변경에 사용될 패턴 문자열 생성
		
		// 2-1) Date 타입 객체의 날짜 포맷 변경 - java.text.SimpleDateFormat 클래스 활용
		// SimpleDateFormat 클래스 인스턴스 생성 시 생성자 파라미터로 패턴 문자열 전달
//		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		// SimpleDateFormat 객체의 format() 메서드 호출하여 파라미터로 전달된 Date 객체 날짜 변환
//		System.out.println(sdf.format(now)); // 변환된 날짜 형식에 맞게 문자열로 리턴됨()
		
		// 2-2) LocalXXX 타입 객체의 날짜 포맷 변경 - java.time.format.DateTimeFormatter 클래스 활용
		// DateTimeFormatter 클래스의 ofPattern() 메서드 호출하여 파라미터로 패턴 문자열 전달
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
		// LocalDate 객체의 format() 메서드 호출하여 파라미터로 DateTimeFormatter 객체 전달하여 날짜 변환
//		System.out.println(today.format(dtf)); // 변환된 날짜 형식에 맞게 문자열로 리턴됨()
		// -----------------
		// 3. 지정한 포맷을 적용하여 날짜 형식 변경 결과를 경로 변수 subDir 에 저장
//		subDir = sdf.format(now); // Date - SimpleDateFormat
		subDir = today.format(dtf); // LocalDate - DateTimeFormatter
		// -----------------
		// 4. 기존 실제 업로드 경로(realPath)에 서브 디렉토리(날짜 경로) 결합(구분자 "/" 추가)
		realPath += "/" + subDir;
//		System.out.println("realPath : " + realPath);
		// => realPath : D:\Shared\Backend\Spring\workspace_spring3\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Spring_MVC_Board\resources\ upload/2024/10/29
		// -----------------
		try {
			// 5. 해당 디렉토리를 실제 경로 상에 생성(단, 존재하지 않는 경로만 자동 생성)
			// 5-1) java.nio.file.Paths 클래스의 get() 메서드 호출하여
			//      실제 업로드 경로를 관리할 java.nio.file.Path 객체 리턴받기
			Path path = Paths.get(realPath); // 파라미터로 실제 업로드 경로 전달
			
			// 5-2) Files 클래스의 createDirectories() 메서드 호출하여 실제 경로 생성
			//      => 파라미터로 Path 객체 전달
			//      => 이 때, 경로 상에서 생성되지 않은 모든 디렉토리를 생성해준다!
			//      => 만약, 최종 서브디렉토리 1개만 생성 시 createDirectory() 메서드도 사용 가능
			Files.createDirectories(path); // IOException 예외 처리 필요(임시로 현재 클래스에서 처리)
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// --------------------------------------------------------------------------------------
		// [ 업로드 되는 실제 파일 처리 ]
		// 실제 파일은 BoardVO 객체의 MultipartFile 타입 객체로 관리함(멤버변수명 fileXXX)
		MultipartFile mFile1 = board.getFile1();
		MultipartFile mFile2 = board.getFile2();
		MultipartFile mFile3 = board.getFile3();
		// => 만약, 복수개의 파일(multiple 속성) 업로드 시 MultipartFile[] 타입으로 관리됨
		//    따라서, 지금부터 수행하는 작업을 MultipartFile[] 배열 반복문 내에서 수행하면 동일
		
		// MultipartFile 객체의 getOriginalFilename() 메서드 호출 시 업로드 한 원본 파일명 리턴
		// => 주의! 업로드 파일이 존재하지 않아도 MultipartFile 객체가 존재함
		//    따라서, 파일명이 null 값이 아닌 널스트링이 리턴됨
		System.out.println("원본파일명1 : " + mFile1.getOriginalFilename());
		System.out.println("원본파일명2 : " + mFile2.getOriginalFilename());
		System.out.println("원본파일명3 : " + mFile3.getOriginalFilename());
		// ------------------------------
		/*
		 * [ 파일명 중복 방지 대책 ]
		 * - 동일한 파일명을 갖는 서로 다른 파일이 같은 디렉토리에 업로드 불가!
		 * - 파일명 앞에 난수를 결합하여 다른 파일과 중복되지 않도록 중복 방지 처리 필수!
		 *   => 숫자만으로 이루어진 난수보다 문자와 함께 결합된 난수가 더 효율적
		 * - 기본 난수 생성 라이브러리(SecureRandom 클래스 등)를 활용하거나
		 *   java.util.UUID 클래스 활용하여 난수 생성 또는 별도의 라이브러리 활용하여 난수 생성 가능
		 *   => UUID : 현재 시스템(서버)에서 랜덤ID 값을 추출하여 제공하는 클래스
		 *             (Universally Unique Identifier : 범용 고유 식별자)
		 */
//		String uuid = UUID.randomUUID().toString();
//		System.out.println("uuid : " + uuid); // 49d85b73-a472-43d7-bcd8-6af55a91267f
		
		// 생성된 UUID 값을 원본 파일명 앞에 결합
		// => UUID 값과 결합 시 원본 파일명 구분을 위해 구분자 "_" 사용(UUID 의 구분자와 다른 문자면 OK)
		//    ex) 49d85b73-a472-43d7-bcd8-6af55a91267f_logo.png
		// => 단, 파일명 길이 조절을 위해 임의로 UUID 중 앞 8자리 문자열만 추출하여 사용
		//    ex) 49d85b73_logo.png
		// uuid 문자열의 substring() 메서드 호출하여 부분 문자열 추출 => 0 ~ 8-1 번 인덱스까지 문자열 추출
//		System.out.println("파일명1 : " + uuid.substring(0, 8) + "_" + mFile1.getOriginalFilename());
//		System.out.println("파일명2 : " + uuid.substring(0, 8) + "_" + mFile2.getOriginalFilename());
//		System.out.println("파일명3 : " + uuid.substring(0, 8) + "_" + mFile3.getOriginalFilename());
		
		// 단, 자신의 업로드 파일명끼리도 중복을 방지하려면 UUID 를 매번 추출하여 결합
//		System.out.println("파일명1 : " + UUID.randomUUID().toString().substring(0, 8) + "_" + mFile1.getOriginalFilename());
//		System.out.println("파일명2 : " + UUID.randomUUID().toString().substring(0, 8) + "_" + mFile2.getOriginalFilename());
//		System.out.println("파일명3 : " + UUID.randomUUID().toString().substring(0, 8) + "_" + mFile3.getOriginalFilename());
//		String fileName1 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile1.getOriginalFilename();
//		String fileName2 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile2.getOriginalFilename();
//		String fileName3 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile3.getOriginalFilename();
		// ---------------
		// 업로드 할 파일이 존재할 경우에만(= 원본 파일명이 널스트링이 아닐 경우에만)
		// BoardVO 객체에 서브 디렉토리명과 함께 난수가 결합된 파일명 저장
		// => 단, 업로드 파일이 선택되지 않은 파일은 BoardVO 객체의 파일명에 null 값이 기본값이므로
		//    DB 컬럼에 NN 제약조건 위반하지 않기 위해 멤버변수값을 널스트링("") 으로 변경
		board.setBoard_file("");
		board.setBoard_file1("");
		board.setBoard_file2("");
		board.setBoard_file3("");
		
		String fileName1 = "";
		String fileName2 = "";
		String fileName3 = "";
		
		// 업로드 파일명이 널스트링이 아닐 경우 판별하여 파일명 저장(각 파일을 별개의 if 문으로 판별)
		if(!mFile1.getOriginalFilename().equals("")) {
			fileName1 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile1.getOriginalFilename();
			board.setBoard_file1(subDir + "/" + fileName1);
		}
		
		if(!mFile2.getOriginalFilename().equals("")) {
			fileName2 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile2.getOriginalFilename();
			board.setBoard_file2(subDir + "/" + fileName2);
		}
		
		if(!mFile3.getOriginalFilename().equals("")) {
			fileName3 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile3.getOriginalFilename();
			board.setBoard_file3(subDir + "/" + fileName3);
		}
		
//		System.out.println("DB 에 저장될 파일명1 : " + board.getBoard_file1());
//		System.out.println("DB 에 저장될 파일명2 : " + board.getBoard_file2());
//		System.out.println("DB 에 저장될 파일명3 : " + board.getBoard_file3());
		// ------------------------------------------------------------------------------------
		// BoardService - registBoard() 메서드 호출하여 게시물 등록 작업 요청
		// => 파라미터 : BoardVO 객체   리턴타입 : int(insertCount)
		int insertCount = boardService.registBoard(board);
//		System.out.println("조회된 가장 큰 글 번호 : " + board.getBoard_num());
		// => 마이바티스 selectKey 로 조회된 결과값이 BoardVO - board_num 에 저장되므로
		//    같은 객체를 공유하는 컨트롤러에서도 해당 조회 결과 확인 가능함
		// --------------
		// 업로드 파일들은 MultipartFile 객체에 의해 임시 저장공간에 저장되어 있으며
		// 글쓰기 작업 성공 시 임시 저장공간 -> 실제 디렉토리오 이동 작업 필수!
		// => MutlipartFile 객체의 transferTo() 메서드 호출하여 실제 위치로 이동 처리
		//    (파라미터 : java.io.File 타입 객체)
		//    (File 객체 생성 시 생성자에 업로드 경로명과 실제 파일명 전달)
		// => 단, 업로드 파일이 선택되지 않은 항목은 이동 대상에서 제외
		// => 만약, 업로드 파일 사이즈가 지정된 사이즈 초과 시 예외 발생함
		//    org.apache.commons.fileupload.FileUploadBase$SizeLimitExceededException: the request was rejected because its size (13066858) exceeds the configured maximum (10485760)
		if(insertCount > 0) { // 성공
			try {
				if(!mFile1.getOriginalFilename().equals("")) {
					mFile1.transferTo(new File(realPath, fileName1));
				}
				
				if(!mFile2.getOriginalFilename().equals("")) {
					mFile2.transferTo(new File(realPath, fileName2));
				}
				
				if(!mFile3.getOriginalFilename().equals("")) {
					mFile3.transferTo(new File(realPath, fileName3));
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// 글목록(BoardList) 서블릿 주소 리다이렉트
			return "redirect:/BoardList";
		} else {
			// "글쓰기 실패!" 메세지 처리를 위해 fail.jsp 페이지 포워딩
			model.addAttribute("msg", "글쓰기 실패!");
			return "result/fail";
		}
	}
	// ======================================================================================
	// [ 글목록 조회 비즈니스 로직("BoardList" - GET) ]
	// - 파라미터 : 검색타입(searchType), 검색어(searchKeyword) => 기본값 널스트링("") 설정
	//              페이지번호(pageNum) => 기본값 1 설정
	//   => 매핑 메서드 파라미터에 기본값 설정을 위해서는 @RequestParam 어노테이션 사용
	//      @RequestParam(defaultValue = "기본값") 데이터타입 변수명       
	@GetMapping("BoardList")
	public String boardList(
			@RequestParam(defaultValue = "") String searchType, 
			@RequestParam(defaultValue = "") String searchKeyword,
			@RequestParam(defaultValue = "1") int pageNum,
			Model model) {
		System.out.println("검색타입 : " + searchType);
		System.out.println("검색어 : " + searchKeyword);
		System.out.println("페이지번호 : " + pageNum);
		// -------------------------------------------------------------------
		// [ 페이징 처리 ]
		// 1. 페이징 처리를 위해 조회 목록 갯수 조절에 사용될 변수 선언 및 계산
		int listLimit = 5; // 페이지 당 게시물 수
		int startRow = (pageNum - 1) * listLimit; // 조회할 게시물의 DB 행 번호(row 값)
		
		// 2. 실제 뷰페이지에서 페이징 처리를 위한 계산 작업
		// BoardService - getBoardListCount() 메서드 호출하여 전체 게시물 수 조회 요청
		// => 파라미터 : 검색타입, 검색어   리턴타입 : int(listCount)
		int listCount = boardService.getBoardListCount(searchType, searchKeyword);
//		System.out.println("전체 게시물 수 : " + listCount);
		
		// 임시) 페이지 당 페이지 번호 갯수를 2개로 지정(1 2 or 3 4...)
		int pageListLimit = 2; 
		// 최대 페이지 번호 계산(전체 게시물 수를 페이지 당 게시물 수로 나눔)
		// => 이 때, 나머지가 0보다 크면 페이지 수 + 1
		int maxPage = listCount / listLimit + (listCount % listLimit > 0 ? 1 : 0);
		// => 단, 최대 페이지 번호가 0 일 경우 1페이지로 변경
		if(maxPage == 0) {
			maxPage = 1;
		}
		
		// 현재 페이지에서 보여줄 시작 페이지 번호 계산(1, 3, 5, 7, 9)
		int startPage = (pageNum - 1) / pageListLimit * pageListLimit + 1;
		// 현재 페이지에서 보여줄 마지막 페이지 번호 계산(2, 4, 6, 8, 10)
		int endPage = startPage + pageListLimit - 1;
		
		// 단, 마지막 페이지번호(endPage) 값이 최대 페이지번호(maxPage)보다 클 경우
		// 마지막 페이지 번호를 최대 페이지번호로 교체
		if(endPage > maxPage) {
			endPage = maxPage;
		}
		
		// 전달받은 페이지번호가 1보다 작거나 최대 페이지 번호보다 클 경우
		// fail.jsp 페이지 포워딩을 통해 "해당 페이지는 존재하지 않습니다!" 출력하고
		// 1페이지로 이동하도록 처리
		if(pageNum < 1 || pageNum > maxPage) {
			model.addAttribute("msg", "해당 페이지는 존재하지 않습니다!");
			model.addAttribute("targetURL", "BoardList?pageNum=1");
			return "result/fail";
		}
		
		// 페이징 정보 관리하는 PageInfo 객체 생성 및 계산 결과 저장
		PageInfo pageInfo = new PageInfo(listCount, pageListLimit, maxPage, startPage, endPage);
		
		// Model 객체에 페이징 정보 저장
		model.addAttribute("pageInfo", pageInfo);
		// -------------------------------------------------------------------
		// BoardService - getBoardList() 메서드 호출하여 게시물 목록 조회 요청
		// => 파라미터 : 검색타입, 검색어, 시작행번호, 게시물 수
		// => 리턴타입 : List<BoardVO>(boardList)
		List<BoardVO> boardList = boardService.getBoardList(searchType, searchKeyword, startRow, listLimit);
		// -------------------------------------------------------------------
		// 조회된 게시물 목록 정보를 Model 객체에 저장
		model.addAttribute("boardList", boardList);
		
		return "board/board_list";
	}
	// ================================================================================
	// [ 글 상세정보 조회 비즈니스 로직("BoardDetail" - GET) ]
	@GetMapping("BoardDetail")
	public String boardDetail(int board_num, Model model) {
		// BoardService - getBoard() 메서드 호출하여 글 상세정보 조회 요청
		// => 파라미터 : 글번호   리턴타입 : BoardVO(board)
//		BoardVO board = boardService.getBoard(board_num);
		// ---------------------------------------------------------------
		// 조회수 증가 작업 추가
		// => getBoard() 메서드 파라미터에 조회수 증가 여부(boolean 타입) 추가
		//    (true : 증가, false : 미증가)
		BoardVO board = boardService.getBoard(board_num, true);
		
		// ---------------------------------------------------------------
		// 만약, 조회 결과가 없을 경우 "fail.jsp" 페이지를 통해 "존재하지 않는 게시물입니다" 처리
		if(board == null) {
			model.addAttribute("msg", "존재하지 않는 게시물입니다");
			return "result/fail";
		}
		
		// Model 객체에 조회 결과 저장
		model.addAttribute("board", board);
		// -----------------------------------------------------------------------
		// 공통 메서드인 addFileListToModel() 메서드 호출하여
		// 뷰페이지에서 파일 목록의 효율적 처리를 위해 별도의 가공 작업 수행
		// => 파라미터 : BoardVO 객체, Model 객체   리턴타입 : void
		// => 전달된 Model 객체에 처리된 파일 목록을 List 객체로 저장함
		addFileListToModel(board, model);
		
		// -----------------------------------------------------
		return "board/board_detail";
	}
	
	// ================================================================================
	// [ 글 삭제 비즈니스 로직("BoardDelete" - GET) ]
	@GetMapping("BoardDelete")
	public String boardDelete(
			BoardVO board, @RequestParam(defaultValue = "1") int pageNum, 
			HttpSession session, HttpServletRequest request, Model model) {
//		System.out.println("board_num = " + board.getBoard_num());
		
		// 미 로그인 처리
		String id = (String)session.getAttribute("sId");
		if(id == null) {
			model.addAttribute("msg", "로그인 필수!\\n로그인 페이지로 이동합니다.");
			model.addAttribute("targetURL", "MemberLogin");
			
			String prevURL = request.getServletPath();
			String queryString = request.getQueryString(); // URL 파라미터 가져오기(없으면 null)
			
			if(queryString != null) {
				prevURL += "?" + queryString;
			}
			
			session.setAttribute("prevURL", prevURL);
			
			return "result/fail";
		}
		// -------------------------------------------------------------------------
		// 게시물 삭제 후 실제 업로드 된 파일도 서버상에서 삭제해야하므로
		// DB 에서 게시물에 해당하는 레코드 삭제 전 파일명을 미리 조회해야함
		// => BoardService - getBoard() 메서드를 컨트롤러에서 재사용하거나
		//    (BoardVO board = boardService.getBoard(board.getBoard_num(), false))
		//    BoardService - removeBoard() 메서드 내에서 게시물 상세정보 조회도 가능
		board = boardService.getBoard(board.getBoard_num(), false); // 조회수 증가하지 않도록 false 전달
		
		// 조회된 게시물이 존재하지 않거나, 조회된 게시물의 작성자가 세션 아이디와 다를 경우
		// "잘못된 접근입니다!" 처리하기 위해 "result/fail.jsp" 페이지 포워딩 처리
		if(board == null || !id.equals(board.getBoard_name())) {
			model.addAttribute("msg", "잘못된 접근입니다!");
			return "result/fail";
		}
		// -------------------------------------------------------------------------
		// BoardService - removeBoard() 메서드 호출하여 게시물 삭제 요청
		// => 파라미터 : 글번호(board_num)   리턴타입 : int(deleteCount)
		int deleteCount = boardService.removeBoard(board.getBoard_num());
		
		// DB 게시물 정보 삭제 처리 결과 판별 후 성공 시 파일 삭제 작업 처리
		if(deleteCount > 0) {
			// -------------------- 서버 상의 파일 삭제 작업 -----------------
			// 실제 업로드 경로 알아내기
			String realPath = session.getServletContext().getRealPath(uploadPath);
			
			// 삭제 작업 코드 중복을 제거하기 위해 
			// 삭제 대상 파일명을 별도의 List 객체에 저장(제네릭타입 : String) 또는 배열 사용 가능
			List<String> fileList = new ArrayList<String>();
			fileList.add(board.getBoard_file1());
			fileList.add(board.getBoard_file2());
			fileList.add(board.getBoard_file3());
//			System.out.println("삭제할 파일 목록 : " + fileList);
			
			// 향상된 for문 활용하여 리스트(또는 배열) 반복
			for(String file : fileList) {
				// 파일명이 널스트링이 아닐 경우에만 파일 삭제
				if(!file.equals("")) {
					// 업로드 경로와 파일명(서브디렉토리 경로 포함) 결합하여 Path 객체 생성
					Path path = Paths.get(realPath, file);
					
					// java.nio.file 패키지의 Files 클래스의 deleteIfExists() 메서드 호출하여
					// 해당 파일이 실제 서버 상에 존재할 경우에만 삭제 처리
					try {
						Files.deleteIfExists(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			// ---------------------------------------------------------------
//			return "redirect:/BoardList" + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
			// BoardList 서블릿에는 글번호 파라미터는 제외시켜야함. 페이지번호만 전달
			return "redirect:/BoardList?pageNum=" + pageNum;
		} else {
			model.addAttribute("msg", "삭제 실패!");
			return "result/fail";
		}
		
	}
	
	// ===================================================================================
	// [ 글수정 폼("BoardModify" - GET) ]
	@GetMapping("BoardModify")
	public String boardModifyForm(int board_num, HttpSession session, Model model, HttpServletRequest request) {
		// 미 로그인 처리
		String id = (String)session.getAttribute("sId");
		if(id == null) {
			model.addAttribute("msg", "로그인 필수!\\n로그인 페이지로 이동합니다.");
			model.addAttribute("targetURL", "MemberLogin");
			
			String prevURL = request.getServletPath();
			String queryString = request.getQueryString(); // URL 파라미터 가져오기(없으면 null)
			
			if(queryString != null) {
				prevURL += "?" + queryString;
			}
			
			session.setAttribute("prevURL", prevURL);
			
			return "result/fail";
		}
		// ----------------------------------------------------------------------------------
		// BoardService - getBoard() 메서드 재사용하여 게시물 1개 정보 조회
		// => 조회수 증가되지 않도록 두번째 파라미터값을 false 로 전달
		BoardVO board = boardService.getBoard(board_num, false);
		
		// 조회된 게시물이 존재하지 않거나, 조회된 게시물의 작성자가 세션 아이디와 다를 경우
		// "잘못된 접근입니다!" 처리하기 위해 "result/fail.jsp" 페이지 포워딩 처리
		if(board == null || !id.equals(board.getBoard_name())) {
			model.addAttribute("msg", "잘못된 접근입니다!");
			return "result/fail";
		}
		
		// 조회 결과 게시물 정보 저장
		model.addAttribute("board", board);
		// -------------------------------------------------------------------------
		// 뷰페이지에서 파일 목록의 효율적 처리를 위해 addFileListToModel() 메서드 활용
		addFileListToModel(board, model);
		
		return "board/board_modify_form";
	}

	// [ 게시물 수정 비즈니스 로직("BoardModify - POST") ]
	@PostMapping("BoardModify")
	public String boardModify(BoardVO board, @RequestParam(defaultValue = "1") int pageNum, 
			HttpSession session, Model model, HttpServletRequest request) {
//		System.out.println(board);
		// ---------------------------------------------------------------------
		// 파일 업로드 처리 준비
		String realPath = getRealPath(session); // 실제 경로 알아내기
		String subDir = createDirectories(realPath); // 디렉토리 생성하기
		// 기존 realPath 경로에 subDir 경로 결합
		realPath += "/" + subDir;
		
		// 파일명 중복방지 대책 수행할 processDulicateFileNames() 메서드 호출
		List<String> fileNames = processDulicateFileNames(board, subDir); // 중복처리된 파일명 리턴받기
//		System.out.println(board);
//		System.out.println(fileNames);
		// ---------------------------------------------------------------------
		// BoardService - modifyBoard() 메서드 호출하여 글 수정 작업 요청
		// => 파라미터 : BoardVO 객체   리턴타입 : int(updateCount)
		int updateCount = boardService.modifyBoard(board);
		
		// 수정 처리 요청 결과 판별
		if(updateCount > 0) {
			// 성공 시 실제 파일 업로드(임시경로 -> 실제경로) 처리를 위해 completeUpload() 메서드 호출
			completeUpload(board, realPath, fileNames);
			
			return "redirect:/BoardDetail?board_num=" + board.getBoard_num() + "&pageNum=" + pageNum;
		} else {
			model.addAttribute("msg", "글 수정 실패!");
			return "result/fail";
		}
		
	}

	
	// ======================================================================================
	// ======================================================================================
	// ======================================================================================
	// 뷰페이지에서 파일 목록의 효율적 처리를 위해 별도의 가공하는 메서드
	// => 파일 정보가 저장된 BoardVO 객체와 최종 결과를 저장할 Model 객체를 파라미터로 전달받기
	private void addFileListToModel(BoardVO board, Model model) {
		// 1. 파일명을 별도의 List 객체에 저장(제네릭타입 : String)
		List<String> fileList = new ArrayList<String>();
		fileList.add(board.getBoard_file1());
		fileList.add(board.getBoard_file2());
		fileList.add(board.getBoard_file3());
//				System.out.println(fileList);
		// --------------
		// 2. 만약, 컨트롤러 측에서 원본 파일명을 추출하여 전달할 경우
		//    => 파일명이 저장된 List 객체를 반복하면서 
		//       원본 파일명을 추출하여 별도의 List 에 저장
		List<String> originalFileList = new ArrayList<String>();
		for(String file : fileList) {
//			System.out.println("file : " + file);
			if(!file.equals("")) {
				// 실제 파일명에서 "_" 기호 다음(인덱스값 + 1)부터 끝까지 추출하여 리스트에 추가
				originalFileList.add(file.substring(file.indexOf("_") + 1));
			} else {
				// 파일이 존재하지 않을 경우 원본 파일명도 파일명과 동일하게(널스트링) 저장
				originalFileList.add(file);
			}
		}
//		System.out.println("originalFileList : " + originalFileList);
		// -----------
		// 3. Model 객체에 파일 목록 객체 2개 저장
		model.addAttribute("fileList", fileList);
		model.addAttribute("originalFileList", originalFileList);
		// Model 객체를 별도로 리턴하지 않아도 객체 자체를 전달받았으므로
		// 메서드 호출한 곳에서 저장된 속성 그대로 공유 가능
	}
	
	// ==========================================================================
	// 파일 업로드에 사용될 실제 업로드 디렉토리 경로를 리턴하는 메서드
	private String getRealPath(HttpSession session) {
//		String uploadPath = "/resources/upload"; // 가상의 경로명 저장(이클립스 프로젝트상의 경로)
		// => 다른 메서드에서도 재사용 가능하도록 멤버변수로 선언
		
		// 가상 경로에 대한 서버상의 실제 경로(톰캣이 관리하는 실제 경로) 알아내기
		// => 이클립스 프로젝트 상에서 업로드 폴더 생성 후 파일 업로드 수행 시
		//    이클립스에 연결된 톰캣이 관리하는 폴더에 업로드 폴더가 생성되기 때문
		//    (외부 톰캣 사용시에도 해당 톰캣 디렉토리 내에 업로드 폴더가 생성됨)
		// => request 객체 또는 session 객체의 getServletContext().getRealPath() 메서드 활용
		//    (파라미터 : 가상의 업로드 경로명)
//		String realPath = request.getServletContext().getRealPath(uploadPath);
		String realPath = session.getServletContext().getRealPath(uploadPath);
//		System.out.println("실제 업로드 경로 : " + realPath);
		// 실제 업로드 경로 : D:\Shared\Backend\Spring\workspace_spring3\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Spring_MVC_Board\resources\ upload
		
		// 실제 경로 리턴
		return realPath;
	}
	
	// 파일 업로드 과정에서 서브디렉토리까지 생성하는 메서드
	// => 생성 후 서브디렉토리명 리턴
	public String createDirectories(String realPath) {
		// [ 경로 관리 ]
		// 업로드 파일에 대한 관리 용이성을 증대시키기 위해 서브(하위) 디렉토리 활용하여 분산 관리
		// => 날짜별로 하위 디렉토리를 분류
		String subDir = ""; // 서브 디렉토리명을 저장할 변수 선언
		
		// 파일 업로드 시점에 맞는 날짜별 서브디렉토리 생성
		// => java.util.Date 또는 java.time.LocalXXX 클래스 활용(LocalXXX 클래스가 더 효율적)
		// 1. 현재 시스템의 날짜 정보를 갖는 객체 생성
		// 1-1) java.util.Date 클래스 활용
//		Date now = new Date(); // 기본 생성자 호출 시 시스템(톰캣)의 현재 날짜 및 시각 정보 생성
//		System.out.println(now); // Tue Oct 29 11:37:27 KST 2024
		
		// 1-2) java.time.LocalXXX 클래스 활용
		// => 날짜 정보만 관리할 경우 LocalDate, 시각 정보는 LocalTime, 날짜 및 시각 정보는 LocalDateTime 클래스 활용
		LocalDate today = LocalDate.now(); // 현재 시스템의 날짜 정보 생성
//		System.out.println(today); // 2024-10-29
		// -----------------------
		// 2. 날짜 포맷을 디렉토리 형식에 맞게 변경(ex. 2024-10-29 => 2024/10/29)
		// => 단, 윈도우 운영체제 기준으로 디렉토리 구분자는 백슬래시(\)로 표기하지만
		//    자바 또는 자바스크립트 문자열로 지정할 때 이스케이스 문자로 취급되므로
		//    백슬래시 2번(\\) 또는 슬래시(/) 기호로 경로 구분자 사용
		String datePattern = "yyyy/MM/dd"; // 날짜 포맷 변경에 사용될 패턴 문자열 생성
		
		// 2-1) Date 타입 객체의 날짜 포맷 변경 - java.text.SimpleDateFormat 클래스 활용
		// SimpleDateFormat 클래스 인스턴스 생성 시 생성자 파라미터로 패턴 문자열 전달
//		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		// SimpleDateFormat 객체의 format() 메서드 호출하여 파라미터로 전달된 Date 객체 날짜 변환
//		System.out.println(sdf.format(now)); // 변환된 날짜 형식에 맞게 문자열로 리턴됨()
		
		// 2-2) LocalXXX 타입 객체의 날짜 포맷 변경 - java.time.format.DateTimeFormatter 클래스 활용
		// DateTimeFormatter 클래스의 ofPattern() 메서드 호출하여 파라미터로 패턴 문자열 전달
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
		// LocalDate 객체의 format() 메서드 호출하여 파라미터로 DateTimeFormatter 객체 전달하여 날짜 변환
//		System.out.println(today.format(dtf)); // 변환된 날짜 형식에 맞게 문자열로 리턴됨()
		// -----------------
		// 3. 지정한 포맷을 적용하여 날짜 형식 변경 결과를 경로 변수 subDir 에 저장
//		subDir = sdf.format(now); // Date - SimpleDateFormat
		subDir = today.format(dtf); // LocalDate - DateTimeFormatter
		// -----------------
		// 4. 기존 실제 업로드 경로(realPath)에 서브 디렉토리(날짜 경로) 결합(구분자 "/" 추가)
		realPath += "/" + subDir;
//		System.out.println("realPath : " + realPath);
		// => realPath : D:\Shared\Backend\Spring\workspace_spring3\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Spring_MVC_Board\resources\ upload/2024/10/29
		// -----------------
		try {
			// 5. 해당 디렉토리를 실제 경로 상에 생성(단, 존재하지 않는 경로만 자동 생성)
			// 5-1) java.nio.file.Paths 클래스의 get() 메서드 호출하여
			//      실제 업로드 경로를 관리할 java.nio.file.Path 객체 리턴받기
			Path path = Paths.get(realPath); // 파라미터로 실제 업로드 경로 전달
			
			// 5-2) Files 클래스의 createDirectories() 메서드 호출하여 실제 경로 생성
			//      => 파라미터로 Path 객체 전달
			//      => 이 때, 경로 상에서 생성되지 않은 모든 디렉토리를 생성해준다!
			//      => 만약, 최종 서브디렉토리 1개만 생성 시 createDirectory() 메서드도 사용 가능
			Files.createDirectories(path); // IOException 예외 처리 필요(임시로 현재 클래스에서 처리)
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 생성된 디렉토리의 서브디렉토리명 리턴
		return subDir;
	}
	
	// =========================================================================================
	// 파일명 중복 대책 처리
	// => 중복 처리된 파일명을 List 객체로 리턴
	private List<String> processDulicateFileNames(BoardVO board, String subDir) {
		// 실제 파일은 BoardVO 객체의 MultipartFile 타입 객체로 관리함(멤버변수명 fileXXX)
		MultipartFile mFile1 = board.getFile1();
		MultipartFile mFile2 = board.getFile2();
		MultipartFile mFile3 = board.getFile3();
		// => 만약, 복수개의 파일(multiple 속성) 업로드 시 MultipartFile[] 타입으로 관리됨
		//    따라서, 지금부터 수행하는 작업을 MultipartFile[] 배열 반복문 내에서 수행하면 동일
		
		// MultipartFile 객체의 getOriginalFilename() 메서드 호출 시 업로드 한 원본 파일명 리턴
		// => 주의! 업로드 파일이 존재하지 않아도 MultipartFile 객체가 존재함
		//    따라서, 파일명이 null 값이 아닌 널스트링이 리턴됨
		System.out.println("원본파일명1 : " + mFile1.getOriginalFilename());
		System.out.println("원본파일명2 : " + mFile2.getOriginalFilename());
		System.out.println("원본파일명3 : " + mFile3.getOriginalFilename());
		// ------------------------------
		/*
		 * [ 파일명 중복 방지 대책 ]
		 * - 동일한 파일명을 갖는 서로 다른 파일이 같은 디렉토리에 업로드 불가!
		 * - 파일명 앞에 난수를 결합하여 다른 파일과 중복되지 않도록 중복 방지 처리 필수!
		 *   => 숫자만으로 이루어진 난수보다 문자와 함께 결합된 난수가 더 효율적
		 * - 기본 난수 생성 라이브러리(SecureRandom 클래스 등)를 활용하거나
		 *   java.util.UUID 클래스 활용하여 난수 생성 또는 별도의 라이브러리 활용하여 난수 생성 가능
		 *   => UUID : 현재 시스템(서버)에서 랜덤ID 값을 추출하여 제공하는 클래스
		 *             (Universally Unique Identifier : 범용 고유 식별자)
		 */
//				String uuid = UUID.randomUUID().toString();
//				System.out.println("uuid : " + uuid); // 49d85b73-a472-43d7-bcd8-6af55a91267f
		
		// 생성된 UUID 값을 원본 파일명 앞에 결합
		// => UUID 값과 결합 시 원본 파일명 구분을 위해 구분자 "_" 사용(UUID 의 구분자와 다른 문자면 OK)
		//    ex) 49d85b73-a472-43d7-bcd8-6af55a91267f_logo.png
		// => 단, 파일명 길이 조절을 위해 임의로 UUID 중 앞 8자리 문자열만 추출하여 사용
		//    ex) 49d85b73_logo.png
		// uuid 문자열의 substring() 메서드 호출하여 부분 문자열 추출 => 0 ~ 8-1 번 인덱스까지 문자열 추출
//				System.out.println("파일명1 : " + uuid.substring(0, 8) + "_" + mFile1.getOriginalFilename());
//				System.out.println("파일명2 : " + uuid.substring(0, 8) + "_" + mFile2.getOriginalFilename());
//				System.out.println("파일명3 : " + uuid.substring(0, 8) + "_" + mFile3.getOriginalFilename());
		
		// 단, 자신의 업로드 파일명끼리도 중복을 방지하려면 UUID 를 매번 추출하여 결합
//				System.out.println("파일명1 : " + UUID.randomUUID().toString().substring(0, 8) + "_" + mFile1.getOriginalFilename());
//				System.out.println("파일명2 : " + UUID.randomUUID().toString().substring(0, 8) + "_" + mFile2.getOriginalFilename());
//				System.out.println("파일명3 : " + UUID.randomUUID().toString().substring(0, 8) + "_" + mFile3.getOriginalFilename());
//				String fileName1 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile1.getOriginalFilename();
//				String fileName2 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile2.getOriginalFilename();
//				String fileName3 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile3.getOriginalFilename();
		// ---------------
		// 업로드 할 파일이 존재할 경우에만(= 원본 파일명이 널스트링이 아닐 경우에만)
		// BoardVO 객체에 서브 디렉토리명과 함께 난수가 결합된 파일명 저장
		// => 단, 업로드 파일이 선택되지 않은 파일은 BoardVO 객체의 파일명에 null 값이 기본값이므로
		//    DB 컬럼에 NN 제약조건 위반하지 않기 위해 멤버변수값을 널스트링("") 으로 변경
		board.setBoard_file("");
		board.setBoard_file1("");
		board.setBoard_file2("");
		board.setBoard_file3("");
		
		String fileName1 = "";
		String fileName2 = "";
		String fileName3 = "";
		
		// 업로드 파일명이 널스트링이 아닐 경우 판별하여 파일명 저장(각 파일을 별개의 if 문으로 판별)
		if(!mFile1.getOriginalFilename().equals("")) {
			fileName1 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile1.getOriginalFilename();
			board.setBoard_file1(subDir + "/" + fileName1);
		}
		
		if(!mFile2.getOriginalFilename().equals("")) {
			fileName2 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile2.getOriginalFilename();
			board.setBoard_file2(subDir + "/" + fileName2);
		}
		
		if(!mFile3.getOriginalFilename().equals("")) {
			fileName3 = UUID.randomUUID().toString().substring(0, 8) + "_" + mFile3.getOriginalFilename();
			board.setBoard_file3(subDir + "/" + fileName3);
		}
		
//				System.out.println("DB 에 저장될 파일명1 : " + board.getBoard_file1());
//				System.out.println("DB 에 저장될 파일명2 : " + board.getBoard_file2());
//				System.out.println("DB 에 저장될 파일명3 : " + board.getBoard_file3());
		// ------------------------------------------------------------------------------------
		// 중복처리된 파일명을 List 객체에 추가 후 리턴
		List<String> fileNames = new ArrayList<String>();
		fileNames.add(fileName1);
		fileNames.add(fileName2);
		fileNames.add(fileName3);
		
		return fileNames;
	}
	
	// ===================================================================================
	// 실제 파일 업로드 처리(임시경로 -> 실제경로)
	private void completeUpload(BoardVO board, String realPath, List<String> fileNames) {
		// 실제 파일은 BoardVO 객체의 MultipartFile 타입 객체로 관리함(멤버변수명 fileXXX)
		MultipartFile mFile1 = board.getFile1();
		MultipartFile mFile2 = board.getFile2();
		MultipartFile mFile3 = board.getFile3();
		
		System.out.println("mFile1 : " + mFile1);
		System.out.println("mFile2 : " + mFile2);
		System.out.println("mFile3 : " + mFile3);
		
		System.out.println("파일명 목록 : " + fileNames);
		System.out.println("realPath : " + realPath);
		System.out.println("boardVO : " + board);
		
		try {
			if(!mFile1.getOriginalFilename().equals("")) {
				System.out.println("첫번째 파일 업로드 - " + fileNames.get(0));
				mFile1.transferTo(new File(realPath, fileNames.get(0)));
			}
			
			if(!mFile2.getOriginalFilename().equals("")) {
				System.out.println("두번째 파일 업로드");
				mFile2.transferTo(new File(realPath, fileNames.get(1)));
			}
			
			if(!mFile3.getOriginalFilename().equals("")) {
				System.out.println("세번째 파일 업로드");
				mFile3.transferTo(new File(realPath, fileNames.get(2)));
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}











