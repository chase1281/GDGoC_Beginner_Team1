package com.example.StudyBoard.auth.entity;

import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomMemberDetailService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public CustomMemberDetails loadMemberByMemberId(final Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NULL_MEMBER));

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + member.getRole().name())
        );

        return CustomMemberDetails.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .password(member.getPassword())
                .authorities(authorities)
                .build();
    }
}
