package com.example.StudyBoard.auth.service;

import com.example.StudyBoard.auth.dto.LoginMemberRequest;
import com.example.StudyBoard.auth.dto.LoginRequest;
import com.example.StudyBoard.auth.dto.LoginSuccessResponse;
import com.example.StudyBoard.auth.repository.TokenRepository;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.dto.request.MemberRegisterRequest;
import com.example.StudyBoard.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AuthServiceTest {
    //로그인, 토큰, 로그아웃 테스트
    @Autowired
    AuthService authService;

    @Autowired
    MemberService memberService;

    @Autowired
    TokenRepository tokenRepository;

    @Test
    @DisplayName("로그인 성공 - 토큰 발급")
    public void successLogin(){
        //given
        memberService.registerMember(new MemberRegisterRequest("test@naver.com", "테스트유저", "12345678"));

        //when
        LoginRequest request = new LoginRequest("test@naver.com", "12345678");
        LoginSuccessResponse response = authService.login(request);
        //then
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.name()).isEqualTo("테스트유저");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    public void failLogin(){
        //given
        memberService.registerMember(new MemberRegisterRequest("test@naver.com", "테스트유저", "12345678"));
        //when
        LoginRequest request = new LoginRequest("test@naver.com", "wrong");
        //then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INCORRECT_PASSWORD);
    }

    @Test
    @DisplayName("로그아웃 시 RefreshToken 삭제 테스트")
    public void successLogout(){
        //given
        memberService.registerMember(new MemberRegisterRequest("test@naver.com", "테스트유저", "12345678"));
        //when
        LoginSuccessResponse login = authService.login(new LoginRequest("test@naver.com", "12345678"));
        authService.logout(new LoginMemberRequest(Long.valueOf(login.memberId())));
        //then
        assertThat(tokenRepository.findAll().isEmpty()).isTrue();
    }
}
