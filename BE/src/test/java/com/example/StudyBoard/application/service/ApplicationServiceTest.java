package com.example.StudyBoard.application.service;

import com.example.StudyBoard.application.entity.Application;
import com.example.StudyBoard.application.repository.ApplicationRepository;
import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.constant.ApplicationStatus;
import com.example.StudyBoard.constant.BoardStatus;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.board.repository.BoardRepository;
import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.member.repository.MemberRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ApplicationServiceTest {
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member writer;
    private Member applicant;
    private Board testBoard;



    private final LocalDateTime FIXED_START = LocalDateTime.now().minusDays(1);
    private final LocalDateTime FIXED_END = FIXED_START.plusDays(7);


    @BeforeEach
    void setup(){
        writer = createMember("writer@test.com", "writer");
        applicant = createMember("applicant@test.com", "applicant");

        testBoard = Board.create(writer, "test board",2,"content",FIXED_START,FIXED_END);
        boardRepository.save(testBoard);
    }

    private Member createMember(String email, String name) {
        Member member = new Member();
        ReflectionTestUtils.setField(member, "email", email);
        ReflectionTestUtils.setField(member, "name", name);
        ReflectionTestUtils.setField(member, "password", "password123");
        ReflectionTestUtils.setField(member, "role", Role.USER);
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("스터디 신청 성공")
    void aply_success(){
        //when
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());

        //then
        boolean exists = applicationRepository.existsByMemberAndBoard(applicant,testBoard);
        assertThat(exists).isTrue();
        assertThat(testBoard.getCurrentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("신청 실패 - 본인 게시글에 신청하는경우")
    void aply_fail_selfApplication(){
        //when&then
        assertThatThrownBy(() -> applicationService.apply(testBoard.getBoardId(), writer.getMemberId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELF_APPLICATION);

    }

    @Test
    @DisplayName("신청실패 - 중복 신청")
    void apply_fail_duplicated(){
        //given
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());

        assertThatThrownBy(() -> applicationService.apply(testBoard.getBoardId(), applicant.getMemberId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",ErrorCode.DUPLICATED_APPLIED);
    }

    @Test
    @DisplayName("신청 취소 성공")
    void cancel_success(){
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        Application application = applicationRepository.findAll().get(0);

        applicationService.cancel(application.getApplicationId(), applicant.getMemberId());

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(testBoard.getCurrentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("신청 승인 성공")
    void accept_success(){
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        Application application = applicationRepository.findAll().get(0);

        applicationService.accept(application.getApplicationId(), writer.getMemberId());
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(testBoard.getCurrentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("신청 승인 실패 - 신청자 외 시도")
    void accept_fail_notWriter(){
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        Application application = applicationRepository.findAll().get(0);

        assertThatThrownBy(() -> applicationService.accept(application.getApplicationId(), application.getMember().getMemberId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",ErrorCode.NOT_WRITER);
    }

    @Test
    @DisplayName("신청 거절 성공")
    void reject_success(){
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        Application application = applicationRepository.findAll().get(0);

        applicationService.reject(application.getApplicationId(), writer.getMemberId());

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(testBoard.getCurrentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("정원 마감 후 보드 상태 변경")
    void capacity_full_boardClose(){
        Member applicant2 = createMember("applicant2@test.com","applicant2");

        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        applicationService.apply(testBoard.getBoardId(), applicant2.getMemberId());

        assertThat(testBoard.getCurrentCount()).isEqualTo(2);
        assertThat(testBoard.getStatus()).isEqualTo(BoardStatus.CLOSED);
    }

    @Test
    @DisplayName("신청취소로 인한 CLOSED->RECRUITING")
    void cancel_reopenBoard(){
        Member applicant2 = createMember("applicant2@test.com","applicant2");

        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        applicationService.apply(testBoard.getBoardId(), applicant2.getMemberId());

        Application application = applicationRepository.findAll().get(0);
        assertThat(testBoard.getStatus()).isEqualTo(BoardStatus.CLOSED);

        applicationService.cancel(application.getApplicationId(), applicant.getMemberId());

        assertThat(testBoard.getCurrentCount()).isEqualTo(1);
        assertThat(testBoard.getStatus()).isEqualTo(BoardStatus.RECRUITING);

    }

    @Test
    @DisplayName("이미 승인된 신청 승인 불가")
    void accept_fail_alreadyProcessed(){
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        Application application = applicationRepository.findAll().get(0);

        applicationService.accept(application.getApplicationId(), writer.getMemberId());

        assertThatThrownBy(() ->
                applicationService.accept(application.getApplicationId(), writer.getMemberId())
        )
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_PROCESSED_APPLICATION);
    }

    @Test
    @DisplayName("이미 거절된 신청 거절 불가")
    void reject_fail_alreadyProcessed(){
        applicationService.apply(testBoard.getBoardId(), applicant.getMemberId());
        Application application = applicationRepository.findAll().get(0);

        applicationService.reject(application.getApplicationId(), writer.getMemberId());
        
        assertThatThrownBy(() ->
                applicationService.reject(application.getApplicationId(), writer.getMemberId())
        )
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_PROCESSED_APPLICATION);
    }




}
