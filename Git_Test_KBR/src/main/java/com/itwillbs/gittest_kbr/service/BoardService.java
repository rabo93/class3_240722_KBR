package com.itwillbs.gittest_kbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itwillbs.gittest_kbr.mapper.BoardMapper;
import com.itwillbs.gittest_kbr.vo.BoardVO;

@Service
public class BoardService {
	@Autowired
	private BoardMapper mapper;
	
	
	// 게시물 등록 요청을 위한 registBoard()
	public int registBoard(BoardVO board) {
		return mapper.insertBoard(board);
	}

	//============================================================================================
	// 게시물 목록 조회 요청을 위한 getBoardList()
	public List<BoardVO> getBoardList(String searchType, String searchKeyword, int startRow, int listLimit) {
		return mapper.selectBoardList(searchType, searchKeyword, startRow, listLimit);
	}

	public int getBoardListCount(String searchType, String searchKeyword) {
		return mapper.selectBoardListCount(searchType, searchKeyword);
	}
	
	
	//============================================================================================
	// 게시물 상세 조회 요청을 위한 getBoard()
//	public BoardVO getBoard(int board_num) {
//		return mapper.selectBoard(board_num);
//	}
	//-------------------------------------------------------------------
	// 게시물 상세 조회 요청을 위한 getBoard()  - 조회수 증가 작업 추가
	public BoardVO getBoard(int board_num, boolean isIncreaseReadcount) {
		// BoardMapper - selectBoard() 메서드 호출하여 게시물 상세정보 조회 요청
		BoardVO board = mapper.selectBoard(board_num);
		
		// 조회결과가 존재하고, 조회수 증가를 수행해야할 경우
		// BoardMapper - updateReadcount() 메서드 호출하여 해당 게시물의 조회수 증가
		// => 단, 조회수가 증가된 게시물의 변경된 조회수 값을 BoardVO 객체에 반영하기 위해
		//    조회가 완료된 BoardVO 객체를 파라미터로 다시 전달(글번호도 포함되어 있음)
		if(board != null && isIncreaseReadcount) {
			mapper.updateReadcount(board);
		}
		
		// 조회 결과 리턴
		return board;
	}
	
	
	//============================================================================================
	// 게시물 삭제 요청을 수행하는 removeBoard()
	public int removeBoard(int board_num) {
		// BoardMapper - deleteBoard()
		return mapper.deleteBoard(board_num);
	}

	
	//============================================================================================
	// 게시물 수정 요청을 수행하는 modifyBoard()
	public int modifyBoard(BoardVO board) {
		// BoardMapper - updateBoard()
		return mapper.updateBoard(board);
	}

	
	
}
