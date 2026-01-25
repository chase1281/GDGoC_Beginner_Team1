package com.example.StudyBoard.board.entity;

import com.example.StudyBoard.constant.BoardStatus;
import com.example.StudyBoard.entity.BaseEntity;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardStatus status;

    @Column(name = "recruitment_start_date", nullable = false)
    private LocalDateTime recruitmentStartDate;

    @Column(name = "recruitment_end_date", nullable = false)
    private LocalDateTime recruitmentEndDate;

//최대정원
    @Column(nullable = false)
    private int capacity;

//현재 신청 인원
    @Column(nullable = false)
    private int currentCount;

//모집 게시글 생성자
    public Board(Member member, String title, int capacity, String content, LocalDateTime recruitmentStartDate, LocalDateTime recruitmentEndDate) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.currentCount = 0;
        this.status = BoardStatus.RECRUITING;
        this.recruitmentStartDate = recruitmentStartDate;
        this.recruitmentEndDate = recruitmentEndDate;
    }

//신청인원 처리
    public void apply(LocalDateTime now) {
        validateRecruitmentAvailable(now);

        this.currentCount++;

        if (this.currentCount >= this.capacity) {
            this.status = BoardStatus.CLOSED;
        }
    }

    private void validateRecruitmentAvailable(LocalDateTime now) {

        if (this.status == BoardStatus.CLOSED) {
            throw new BusinessException(ErrorCode.RECRUITMENT_CLOSED);
        }

        if (now.isBefore(recruitmentStartDate) || now.isAfter(recruitmentEndDate)) {
            throw new BusinessException(ErrorCode.RECRUITMENT_PERIOD_INVALID);
        }

        if (this.currentCount >= this.capacity) {
            throw new BusinessException(ErrorCode.CAPACITY_EXCEEDED);
        }
    }

    public static Board create(
            Member member,
            String title,
            int capacity,
            String content,
            LocalDateTime start,
            LocalDateTime end

    ) {

        if (member == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if (capacity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!start.isBefore(end)) {
            throw new BusinessException(ErrorCode.INVALID_RECRUITMENT_PERIOD);
        }
        return new Board(member, title, capacity, content, start, end);
    }

    public void release(){
        if(this.currentCount > 0) {
            this.currentCount--;
        }

        if(this.status == BoardStatus.CLOSED && this.currentCount <this.capacity) {
            this.status = BoardStatus.RECRUITING;
        }
    }
}