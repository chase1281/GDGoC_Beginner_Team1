package com.example.StudyBoard.admin.service;

import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMemberService {
    private final MemberRepository memberRepository;

    public void delete(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if(member.getRole() == Role.ADMIN){
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ADMIN);
        }
        memberRepository.delete(member);
    }
}
