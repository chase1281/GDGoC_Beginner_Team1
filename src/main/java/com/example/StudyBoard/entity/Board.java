package com.example.StudyBoard.entity;

import com.example.StudyBoard.constant.BoardStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseEntity{

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
}
