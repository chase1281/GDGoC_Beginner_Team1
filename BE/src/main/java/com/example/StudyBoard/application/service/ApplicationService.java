package com.example.StudyBoard.application.service;

import com.example.StudyBoard.application.entity.Application;
import com.example.StudyBoard.application.repository.ApplicationRepository;
import com.example.StudyBoard.board.entity.Board;
import com.example.StudyBoard.board.repository.BoardRepository;
import com.example.StudyBoard.constant.ApplicationStatus;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //신청
    public void apply(Long boardId, Long memberId) {

        //게시글 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        //회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NULL_MEMBER));

        //본인 글 신청 방지
        if (board.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.SELF_APPLICATION);
        }

        //중복 신청 방지
        if (applicationRepository.existsByMemberAndBoard(member, board)) {
            throw new BusinessException(ErrorCode.DUPLICATED_APPLIED);
        }

        //Board 모집 가능 여부 + 정원 처리
        board.apply(LocalDateTime.now());

        //Application 생성
        Application application = Application.create(member, board);
        applicationRepository.save(application);
    }

    //신청 취소 (신청자)
    public void cancel(Long applicationId, Long memberId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        // 본인 신청만 취소 가능
        if (!application.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        application.reject();
    }

     //신청 승인
    public void accept(Long applicationId, Long memberId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        Board board = application.getBoard();

        // 게시글 작성자만 승인 가능
        if (!board.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.NOT_WRITER);
        }

        // 이미 처리된 신청은 다시 처리 불가
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        application.accept();
    }

    //신청 거절 (게시글 작성자)
    public void reject(Long applicationId, Long memberId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        Board board = application.getBoard();

        // 게시글 작성자만 거절 가능
        if (!board.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.NOT_WRITER);
        }

        // 이미 처리된 신청은 다시 처리 불가
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        application.reject();
    }
}