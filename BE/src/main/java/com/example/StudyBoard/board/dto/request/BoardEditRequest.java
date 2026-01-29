package com.example.StudyBoard.board.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardEditRequest { // record -> class로 변경

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @Min(value = 1, message = "모집 인원은 최소 1명 이상이어야 합니다")
    private int capacity;

    @NotNull(message = "모집 시작일을 입력해주세요")
    private LocalDateTime recruitmentStartDate;

    @NotNull(message = "모집 종료일을 입력해주세요")
    private LocalDateTime recruitmentEndDate;

}
