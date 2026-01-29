package com.example.StudyBoard.member.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {
    //회원가입, 내 정보 요청 테스트
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 요청 성공 테스트")
    public void SuccessRegister() throws Exception{
        //given
        MemberRegisterRequest request = new MemberRegisterRequest(
                "test@naver.com",
                "테스트유저",
                "password1234"
        );

        // when
        // then
        mockMvc.perform(post("/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입 요청 실패 테스트 - 이메일 형식 오류")
    public void failRegister() throws Exception{
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
                .andExpect(status().isBadRequest());
    }
}