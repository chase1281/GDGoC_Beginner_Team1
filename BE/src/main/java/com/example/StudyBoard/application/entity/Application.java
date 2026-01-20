package com.example.StudyBoard.application.entity;

import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.constant.ApplicationStatus;
import com.example.StudyBoard.entity.BaseEntity;
import com.example.StudyBoard.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    //
    @Column(nullable = false)
    private LocalDateTime appliedAt;

    private Application(Member member, Board board) {
        this.member = member;
        this.board = board;
        this.status = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
    }

    public static Application create(Member member, Board board) {
        return new Application(member, board);
    }

    public void accept() {
        this.status = ApplicationStatus.ACCEPTED;
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

}
