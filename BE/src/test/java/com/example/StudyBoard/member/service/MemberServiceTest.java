package com.example.StudyBoard.member.service;

import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.dto.request.MemberRegisterRequest;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MemberServiceTest {
    //회원가입, 내 정보 테스트
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 시 회원이 저장되고 비밀번호는 암호화되는 지 테스트")
    public void register(){
        //given
        MemberRegisterRequest request = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저",
                "password1234"
        );

        //when
        memberService.registerMember(request);

        //then

        Member member = memberRepository.findByEmail(request.email()).orElseThrow();
        assertThat(member.getName()).isEqualTo(request.name());
        assertThat(passwordEncoder.matches(
                request.password(),
                member.getPassword()
        )).isTrue();
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복 이메일")
    public void duplicatedEmail(){
        //given
        MemberRegisterRequest registerRequest = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저",
                "12345678"
        );

        //when
        memberService.registerMember(registerRequest);
        //then
        assertThatThrownBy(() -> memberService.registerMember(registerRequest))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATED_EMAIL);
    }
}
