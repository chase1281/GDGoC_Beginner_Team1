package com.example.StudyBoard.board.dto.response;

import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.constant.BoardStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponse {
    private Long boardId;
    private String title;
    private String content;
    private int capacity;
    private int currentCount;
    private BoardStatus status;
    private LocalDateTime recruitmentStartDate;
    private LocalDateTime recruitmentEndDate;

    //작성자 정보
    private Long memberId;
    private String writerName;


    public static BoardResponse from(Board board){
        return BoardResponse.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .capacity(board.getCapacity())
                .currentCount(board.getCurrentCount())
                .status(board.getStatus())
                .recruitmentStartDate(board.getRecruitmentStartDate())
                .recruitmentEndDate(board.getRecruitmentEndDate())

                .memberId(board.getMember().getMemberId())
                .writerName(board.getMember().getName())
                .build();
    }
}
