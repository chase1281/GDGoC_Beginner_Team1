package com.example.StudyBoard.service;

import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.dto.request.MemberLoginRequest;
import com.example.StudyBoard.dto.request.MemberRegisterRequest;
import com.example.StudyBoard.dto.response.MemberInfoResponse;
import com.example.StudyBoard.entity.Member;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.repository.MemberRepository;
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

        if(memberRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.DUPLICATED_NAME);
        }
        Member member = new Member(request.email(), request.name(), passwordEncoder.encode(request.password()), Role.USER);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NULL_MEMBER));

        return new MemberInfoResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getRole()
        );
    }
}
