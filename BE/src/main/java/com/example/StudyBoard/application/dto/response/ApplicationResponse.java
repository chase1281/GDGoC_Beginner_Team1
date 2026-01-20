package com.example.StudyBoard.application.dto.response;

import com.example.StudyBoard.application.entity.Application;
import com.example.StudyBoard.constant.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long applicationId,
        Long boardId,
        Long memberId,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {

    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getApplicationId(),
                application.getBoard().getBoardId(),
                application.getMember().getMemberId(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}