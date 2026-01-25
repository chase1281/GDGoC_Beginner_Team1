package com.example.StudyBoard.member.service;

import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.member.dto.request.MemberRegisterRequest;
import com.example.StudyBoard.member.dto.response.MemberInfoResponse;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerMember(MemberRegisterRequest request){
        if(memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }

        Member member = new Member(request.email(), request.name(), passwordEncoder.encode(request.password()));
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberInfoResponse.from(member);
    }
}
