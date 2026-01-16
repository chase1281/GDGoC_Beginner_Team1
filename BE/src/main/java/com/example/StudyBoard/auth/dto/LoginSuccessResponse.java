package com.example.StudyBoard.auth.dto;

public record LoginSuccessResponse(
        String accessToken,
        String refreshToken,
        String memberId,
        String role
) {
}
