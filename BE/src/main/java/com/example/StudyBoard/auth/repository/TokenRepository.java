package com.example.StudyBoard.auth.repository;

import com.example.StudyBoard.auth.entity.RefreshToken;
import com.example.StudyBoard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);

    void deleteAllByMember(Member member);
}
