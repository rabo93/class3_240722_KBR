package com.itwillbs.gittest_kbr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.itwillbs.gittest_kbr.vo.BoardVO;

@Mapper
public interface BoardMapper {
	
	// 게시물 등록
	int insertBoard(BoardVO board);

	
	// 게시물 목록 조회(복수개 파라미터 전달 시 @Param 어노테이션을 통해 파라미터명 지정 필수!)
//		List<BoardVO> selectBoardList(String searchType, String searchKeyword, int startRow, int listLimit);
	/*
	 * < 게시물 목록 조회 주의사항 >
	 * - Mapper 에 전달된 파라미터가 복수개일 경우 마이바티스가 각 파라미터명을 구별하지 못하므로
	 *   SQL 구문 실행 시 데이터 전달을 위한 파라미터명 지정 시 오류(예외) 발생한다!
	 *   (org.apache.ibatis.binding.BindingException: Parameter 'startRow' not found. Available parameters are [arg1, arg0, param1, param2])
	 * - 반드시 Mapper 내의 메서드 정의 시 파라미터 변수 앞에 @Param 어노테이션을 적용해야한다!
	 * - 기본 문법 : @Param("파라미터명") 데이터타입 변수명
	 *   => 주의! "파라미터명" 과 실제 변수명은 동일할 필요가 없으나,
	 *      "파라미터명" 으로 지정한 이름을 SQL 구문에서 #{파라미터명} 형식으로 사용해야한다!   
	*/
		List<BoardVO> selectBoardList(
				@Param("searchType") String searchType, 
				@Param("searchKeyword") String searchKeyword, 
				@Param("startRow") int startRow, 
				@Param("listLimit") int listLimit);

		// 전체 게시물 수 조회
//		int selectBoardListCount();
		
		// 전체 게시물 수 조회(검색어 기능에 추가로 인해 검색어에 대한 게시물 수 조회로 변경)
		int selectBoardListCount(
				@Param("searchType") String searchType, 
				@Param("searchKeyword") String searchKeyword);

		// 게시물 상세정보 조회
		BoardVO selectBoard(int board_num);

		// 게시물 조회수 증가
		void updateReadcount(BoardVO board);

		// 게시물 삭제
		int deleteBoard(int board_num);

		// 게시물 수정
		int updateBoard(BoardVO board);

	
}
