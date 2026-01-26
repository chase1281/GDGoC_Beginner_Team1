package com.example.StudyBoard.board.dto.request;

import java.time.LocalDateTime;

public record BoardEditRequest(
        String title,
        String content,
        int capacity,
        LocalDateTime recruitmentStartDate,
        LocalDateTime recruitmentEndDate
) {
}
