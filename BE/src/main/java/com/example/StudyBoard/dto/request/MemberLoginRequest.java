package com.example.StudyBoard.dto.request;

public record MemberLoginRequest(
        String email,
        String password
) {
}
