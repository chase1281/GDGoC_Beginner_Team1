package com.example.StudyBoard.member.controller;

import com.example.StudyBoard.auth.dto.LoginRequest;
import com.example.StudyBoard.member.dto.request.MemberRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("회원가입 성공 테스트")
    public void register() throws Exception{
        //given
        MemberRegisterRequest request = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저",
                "password1234"
        );
        //when

        //then
        mockMvc.perform(post("/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }


    @Test
    @DisplayName("회원가입 실패 테스트")
    public void failedRegister() throws Exception{
        //given
        MemberRegisterRequest request = new MemberRegisterRequest(
                "not-email-format",
                "테스트유저",
                "password1234"
        );

        // when
        // then
        mockMvc.perform(post("/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("-001"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void login() throws Exception{
        //given
        MemberRegisterRequest registerRequest = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저",
                "12345678"
        );
        //when
        mockMvc.perform(post("/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(
                "test@naver.com",
                "12345678"
        );
        //then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트유저"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.accessToken").exists()) // 토큰 존재 여부 추가 검증
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    public void failedLogin() throws Exception{
        //given
        MemberRegisterRequest registerRequest = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저",
                "12345678"
        );
        //when
        mockMvc.perform(post("/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        LoginRequest loginRequest = new LoginRequest(
                "test@naver.com",
                "1234567810"
        );
        //then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("-101"))
                .andExpect(jsonPath("$.errorDescription").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복 이메일")
    public void duplicatedEmail() throws Exception{
        //given
        MemberRegisterRequest registerRequest = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저",
                "12345678"
        );

        mockMvc.perform(post("/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        //when
        MemberRegisterRequest duplicateRequest = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저2",
                "12345678"
        );
        //then
        mockMvc.perform(post("/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("-100"))
                .andExpect(jsonPath("$.errorDescription").value("이미 가입된 이메일입니다."));
    }
}