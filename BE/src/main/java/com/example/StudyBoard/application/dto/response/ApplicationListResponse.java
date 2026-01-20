package com.example.StudyBoard.application.dto.response;

import com.example.StudyBoard.constant.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationListResponse(
        Long applicationId,
        String applicantName,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
}