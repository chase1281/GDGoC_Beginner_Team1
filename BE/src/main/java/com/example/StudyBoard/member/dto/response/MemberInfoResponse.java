package com.example.StudyBoard.member.dto.response;

import com.example.StudyBoard.constant.Role;

public record MemberInfoResponse (
        Long memberId,
        String email,
        String name,
        Role role
){
}
