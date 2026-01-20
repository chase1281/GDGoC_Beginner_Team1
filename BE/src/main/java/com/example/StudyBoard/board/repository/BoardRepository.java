package com.example.StudyBoard.board.repository;

import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.constant.BoardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByStatus(BoardStatus status);
}
