package com.example.StudyBoard.admin.controller;

import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.board.repository.BoardRepository;
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

import java.time.LocalDateTime;

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

    @Autowired
    BoardRepository boardRepository;

    @Test
    @DisplayName("관리자는 회원 삭제 가능 테스트")
    @WithMockUser(roles = "ADMIN")
    public void adminDeleteSuccessMemberTest() throws Exception{
        //given
        Member member = memberRepository.save(new Member("user1@test.com", "사용자", "12345678"));

        //when & then
        mockMvc.perform(delete("/admin/members/{memberId}", member.getMemberId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("일반 회원은 회원 삭제 불가 테스트")
    @WithMockUser(roles = "USER")
    public void userDeleteFailMemberTest() throws Exception{
        //given
        Member member = memberRepository.save(new Member("user2@test.com", "사용자", "12345678"));

        //when & then
        mockMvc.perform(delete("/admin/members/{memberId}", member.getMemberId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자는 게시글 삭제 가능 테스트")
    @WithMockUser(roles = "ADMIN")
    public void adminDeleteSuccessBoardTest() throws Exception{
        //given
        Member member = memberRepository.save(
                new Member("user@test.com", "작성자", "12345678")
        );

        Board board = boardRepository.save(
                Board.create(
                        member,
                        "제목",
                        5,
                        "내용",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1)
                )
        );
        //when

        //then
    mockMvc.perform(delete("/admin/boards/{id}", board.getBoardId()))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("일반 회원은 게시글 삭제 불가 테스트")
    @WithMockUser(roles = "USER")
    public void userDeleteFailBoardTest() throws Exception{
        //given
        Member member = memberRepository.save(
                new Member("user@test.com", "작성자", "12345678")
        );

        Board board = boardRepository.save(
                Board.create(
                        member,
                        "제목",
                        5,
                        "내용",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1)
                )
        );
        //when

        //then
        mockMvc.perform(delete("/admin/boards/{id}", board.getBoardId()))
                .andExpect(status().isForbidden());
    }
}