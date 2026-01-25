package com.example.StudyBoard.admin.service;

import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.board.repository.BoardRepository;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminBoardService {
    private final BoardRepository boardRepository;

    public void delete(Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        boardRepository.delete(board);
    }
}
