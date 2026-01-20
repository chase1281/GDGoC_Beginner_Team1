package com.example.StudyBoard.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApplicationStatusChangeRequest(
        @NotNull Long applicationId
) {
}