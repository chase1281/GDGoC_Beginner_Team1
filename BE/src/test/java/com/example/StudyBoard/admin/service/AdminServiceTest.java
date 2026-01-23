package com.example.StudyBoard.admin.service;

import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.member.dto.request.MemberRegisterRequest;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AdminServiceTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AdminMemberService adminMemberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("관리자 계정 삭제 불가 테스트")
    public void deleteFailAdminTest() throws Exception{
        //given
        Member admin = memberRepository.save(new Member("admin1@test.com", "관리자", "1234"));
        ReflectionTestUtils.setField(admin, "role", Role.ADMIN);
        memberRepository.save(admin);
        //when & then
        assertThatThrownBy(() -> adminMemberService.delete(admin.getMemberId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("관리자는 삭제할 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 회원 삭제 테스트")
    public void deleteFailNoMemberTest() throws Exception{
        //given

        //when
        assertThatThrownBy(() -> adminMemberService.delete(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 회원입니다");
        //then

    }
}
