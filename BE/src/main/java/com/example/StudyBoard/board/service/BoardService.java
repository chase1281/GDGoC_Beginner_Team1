package com.example.StudyBoard.board.service;

import com.example.StudyBoard.board.dto.request.BoardCreateRequest;
import com.example.StudyBoard.board.dto.response.BoardResponse;
import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.board.repository.BoardRepository;
import com.example.StudyBoard.constant.BoardStatus; // Import 추가됨
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Import 추가됨
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //게시글 생성
    public BoardResponse create(BoardCreateRequest request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Board board = Board.create(
                member,
                request.getTitle(),
                request.getCapacity(),
                request.getContent(),
                request.getRecruitmentStartDate(),
                request.getRecruitmentEndDate()
        );

        Board savedBoard = boardRepository.save(board);
        return BoardResponse.from(savedBoard);
    }

    //단건 조회
    @Transactional(readOnly = true)
    public BoardResponse get(Long boardId) {
        return BoardResponse.from(findBoard(boardId));
    }

    // 모집중인 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponse> getRecruitingBoards() {
        return boardRepository.findAllByStatus(BoardStatus.RECRUITING)
                .stream()
                .map(BoardResponse::from)
                .toList();
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
    }
}