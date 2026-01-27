package com.example.StudyBoard.board.service;

import com.example.StudyBoard.board.dto.request.BoardCreateRequest;
import com.example.StudyBoard.board.dto.request.BoardEditRequest;
import com.example.StudyBoard.board.dto.response.BoardResponse;
import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.board.repository.BoardRepository;
import com.example.StudyBoard.constant.BoardStatus;
import com.example.StudyBoard.constant.Role;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        //member 생성
        testMember = new Member();
        ReflectionTestUtils.setField(testMember, "email", "test1@naver.com");
        ReflectionTestUtils.setField(testMember, "name", "test1");
        ReflectionTestUtils.setField(testMember, "password", "password123");
        ReflectionTestUtils.setField(testMember, "role", Role.USER);
        memberRepository.save(testMember);
    }
    //board 생성
    private BoardCreateRequest createBoardRequest(
            String title,
            String content,
            int capacity,
            LocalDateTime start,
            LocalDateTime end
    ) {
        BoardCreateRequest request = new BoardCreateRequest();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "content", content);
        ReflectionTestUtils.setField(request, "capacity", capacity);
        ReflectionTestUtils.setField(request, "recruitmentStartDate", start);
        ReflectionTestUtils.setField(request, "recruitmentEndDate", end);
        return request;
    }

    private BoardEditRequest createEditRequest(
            String title,
            String content,
            int capacity,
            LocalDateTime start,
            LocalDateTime end
    ) {
        BoardEditRequest request = new BoardEditRequest();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "content", content);
        ReflectionTestUtils.setField(request, "capacity", capacity);
        ReflectionTestUtils.setField(request, "recruitmentStartDate", start);
        ReflectionTestUtils.setField(request, "recruitmentEndDate", end);
        return request;
    }


    @Test
    @DisplayName("게시글 생성 태스트")
    void createBoard_success() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);

        BoardCreateRequest request =
                createBoardRequest("test board-1", "test -1", 5, start, end);

        // when
        BoardResponse response =
                boardService.create(request, testMember.getMemberId());

        // then
        assertThat(response.getTitle()).isEqualTo("test board-1");
        assertThat(response.getContent()).isEqualTo("test -1");
        assertThat(response.getCapacity()).isEqualTo(5);
        assertThat(response.getRecruitmentStartDate()).isEqualTo(start);
        assertThat(response.getRecruitmentEndDate()).isEqualTo(end);
        assertThat(response.getWriterName()).isEqualTo(testMember.getName());
        assertThat(response.getStatus()).isEqualTo(BoardStatus.RECRUITING);
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void editBoard_success() {
        // given
        Board board = Board.create(
                testMember,
                "test board",
                5,
                "test content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        boardRepository.save(board);

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(14);

        BoardEditRequest editRequest =
                createEditRequest("edit board", "edit content", 10, start, end);

        // when
        BoardResponse response =
                boardService.edit(board.getBoardId(), testMember.getMemberId(), editRequest);

        // then
        assertThat(response.getTitle()).isEqualTo("edit board");
        assertThat(response.getContent()).isEqualTo("edit content");
        assertThat(response.getCapacity()).isEqualTo(10);
        assertThat(response.getRecruitmentStartDate()).isEqualTo(start);
        assertThat(response.getRecruitmentEndDate()).isEqualTo(end);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 작성자가 아님")
    void editBoard_fail_notWriter() {
        // given
        Board board = Board.create(
                testMember,
                "test2 edit board",
                5,
                "test2 edit content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        boardRepository.save(board);
        //test2 member 생성
        Member other = new Member();
        ReflectionTestUtils.setField(other, "email", "test2@naver.com");
        ReflectionTestUtils.setField(other, "name", "test2");
        ReflectionTestUtils.setField(other, "password", "password123");
        ReflectionTestUtils.setField(other, "role", Role.USER);
        memberRepository.save(other);

        BoardEditRequest editRequest =
                createEditRequest("edit title", "edit content", 5,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        // when & then
        assertThatThrownBy(() ->
                boardService.edit(board.getBoardId(), other.getMemberId(), editRequest)
        )
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteBoard_success() {
        // given
        Board board = Board.create(
                testMember,
                "delete test board",
                5,
                "delete test content",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        boardRepository.save(board);

        // when
        boardService.delete(board.getBoardId(), testMember.getMemberId());

        // then
        assertThat(boardRepository.findById(board.getBoardId())).isEmpty();
    }

    @Test
    @DisplayName("모집 중인 게시글 목록 조회")
    void getRecruitingBoards() {
        // given
        Board board1 = Board.create(
                testMember, "test title 1", 5, "content 1",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7)
        );
        Board board2 = Board.create(
                testMember, "test title 2", 3, "content 2",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7)
        );
        boardRepository.saveAll(List.of(board1, board2));

        // when
        List<BoardResponse> responses = boardService.getRecruitingBoards();

        // then
        assertThat(responses).hasSizeGreaterThanOrEqualTo(2);
        assertThat(responses)
                .extracting(BoardResponse::getTitle)
                .contains("test title 1", "test title 2");
        assertThat(responses)
                .allMatch(r -> r.getStatus() == BoardStatus.RECRUITING);
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void getBoard_success() {
        // given
        Board board = Board.create(
                testMember,
                "test title 1",
                5,
                "test content 1",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        boardRepository.save(board);

        // when
        BoardResponse response = boardService.get(board.getBoardId());

        // then
        assertThat(response.getTitle()).isEqualTo("test title 1");
        assertThat(response.getContent()).isEqualTo("test content 1");
        assertThat(response.getCapacity()).isEqualTo(5);
        assertThat(response.getWriterName()).isEqualTo(testMember.getName());
        assertThat(response.getStatus()).isEqualTo(BoardStatus.RECRUITING);
    }
}
