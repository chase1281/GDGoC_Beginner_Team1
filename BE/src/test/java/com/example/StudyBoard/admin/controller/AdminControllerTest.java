package com.example.StudyBoard.admin.controller;

import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("관리자는 회원 삭제 가능 테스트")
    @WithMockUser(roles = "ADMIN")
    public void adminDeleteSuccessMemberTest() throws Exception{
        //given
        Member member = memberRepository.save(new Member("user1@test.com", "사용자", "1234"));

        //when & then
        mockMvc.perform(delete("/admin/members/{memberId}", member.getMemberId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("일반 회원은 회원 삭제 불가 테스트")
    @WithMockUser(roles = "USER")
    public void userDeleteFailMemberTest() throws Exception{
        //given
        Member member = memberRepository.save(new Member("user2@test.com", "유저", "1234"));

        //when & then
        mockMvc.perform(delete("/admin/members/{memberId}", member.getMemberId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자는 게시글 삭제 가능 테스트")
    public void adminDeleteSuccessBoardTest() throws Exception{

    }

    @Test
    @DisplayName("일반 회원은 게시글 삭제 불가 테스트")
    public void userDeleteFailBoardTest() throws Exception{

    }
}
