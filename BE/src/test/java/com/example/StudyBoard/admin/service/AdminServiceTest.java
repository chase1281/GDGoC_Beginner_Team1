package com.example.StudyBoard.admin.service;

import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AdminServiceTest {

    @Autowired
    AdminMemberService adminMemberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("관리자 계정 삭제 불가 테스트")
    public void deleteFailAdminTest(){
        //given
        Member admin = memberRepository.save(new Member("admin1@test.com", "관리자", "12345678"));
        ReflectionTestUtils.setField(admin, "role", Role.ADMIN);
        memberRepository.save(admin);
        //when & then
        assertThatThrownBy(() -> adminMemberService.delete(admin.getMemberId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("관리자는 삭제할 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 회원 삭제 테스트")
    public void deleteFailNoMemberTest(){
        //given

        //when
        assertThatThrownBy(() -> adminMemberService.delete(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 회원입니다");
        //then

    }

    @Test
    @DisplayName("일반 회원 삭제 성공 테스트")
    public void deleteSuccessMemberTest(){
        //given
        Member member = memberRepository.save(new Member("user@naver.com", "사용자", "12345678"));
        //when
        adminMemberService.delete(member.getMemberId());
        //then
        assertThat(memberRepository.findById(member.getMemberId()))
                .isEmpty();
    }
}