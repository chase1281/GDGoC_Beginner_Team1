package com.example.StudyBoard.auth.dto;

import com.example.StudyBoard.constant.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public record MemberDetailRequest(
        @NotNull Long memberId,
        @NotNull Collection<Role> authorities
        ) {
}
