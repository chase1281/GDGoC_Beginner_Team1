package com.example.StudyBoard.board.repository;

import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.constant.BoardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByStatus(BoardStatus status, Pageable pageable);
}
