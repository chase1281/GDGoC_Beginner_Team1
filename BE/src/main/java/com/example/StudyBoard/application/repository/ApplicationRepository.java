package com.example.StudyBoard.application.repository;

import com.example.StudyBoard.application.entity.Application;
import com.example.StudyBoard.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.StudyBoard.member.entity.Member;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByMemberAndBoard(Member member, Board board);
    Page<Application> findAllByMember_MemberId(Long memberId, Pageable pageable);
}
