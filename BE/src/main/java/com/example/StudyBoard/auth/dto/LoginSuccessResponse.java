package com.example.StudyBoard.auth.dto;

public record LoginSuccessResponse(
        String accessToken,
        String refreshToken,
        String memberId,
        String name,
        String role
) {
}
