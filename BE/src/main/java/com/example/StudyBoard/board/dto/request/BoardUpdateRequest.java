package com.example.StudyBoard.board.dto.request;

import com.example.StudyBoard.constant.BoardStatus;

public record BoardUpdateRequest(
        String title,
        String content,
        BoardStatus status) {
}
