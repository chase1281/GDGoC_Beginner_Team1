package com.example.StudyBoard.member.dto.response;

import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.member.entity.Member;

public record MemberInfoResponse (
        Long memberId,
        String email,
        String name,
        Role role
){
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getRole()
        );
    }
}
