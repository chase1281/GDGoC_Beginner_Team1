package com.example.StudyBoard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //공통
    SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "-000", "서버 에러"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "-001", "입력값이 올바르지 않습니다."),

    //회원
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "-100", "이미 가입된 이메일입니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "-101", "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "-102", "존재하지 않는 회원입니다."),
    CANNOT_DELETE_ADMIN(HttpStatus.BAD_REQUEST, "-103", "관리자는 삭제할 수 없습니다."),

    //인증/권한
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "-200", "로그인이 필요합니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "-201", "접근 권한이 없습니다."),
    NOT_WRITER(HttpStatus.FORBIDDEN, "-202", "작성자만 수정/삭제할 수 있습니다."),

    //게시글
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "-300", "존재하지 않는 게시글입니다."),
    RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "-301", "모집이 마감된 게시글입니다."),
    RECRUITMENT_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "-302","모집 기간이 아닙니다."),
    INVALID_RECRUITMENT_PERIOD(HttpStatus.BAD_REQUEST, "-303", "모집 시작일은 종료일보다 이전이어야 합니다."),
    INVALID_STUDY_PERIOD(HttpStatus.BAD_REQUEST, "-304", "스터디 시작일은 종료일보다 이전이어야 합니다."),
    INVALID_PERIOD_SEQUENCE(HttpStatus.BAD_REQUEST, "-305", "스터디 시작일은 모집 종료일 이후여야 합니다."),
    //신청
    DUPLICATED_APPLIED(HttpStatus.BAD_REQUEST, "-400", "이미 신청한 게시글입니다."),
    SELF_APPLICATION(HttpStatus.BAD_REQUEST, "-401", "본인의 게시글에는 신청할 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "-402", "신청 내역을 찾을 수 없습니다."),
    CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "-403", "모집 정원이 초과되었습니다."),
    CANNOT_CANCEL_APPLICATION(HttpStatus.BAD_REQUEST,"-404","취소할 수 없는 신청입니다."),
    ALREADY_PROCESSED_APPLICATION(HttpStatus.BAD_REQUEST, "-405", "이미 처리된 신청입니다."),

    //토큰
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "-T1", "올바르지 않은 AccessToken입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "-T2", "만료된 AccessToken입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "-T3", "만료된 RefreshToken입니다."),
    NULL_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "-T4", "존재하지 않은 RefreshToken 접근"),
    NOT_ACCESS_TOKEN_FOR_REISSUE(HttpStatus.BAD_REQUEST, "-T5", "재발급하기에는 유효기간이 남은 AccessToken"),
    TOKEN_MISSING_AUTHORITY(HttpStatus.BAD_REQUEST, "-T6", "권한 정보가 담겨있지 않은 토큰입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorDescription;

    ErrorCode(HttpStatus httpStatus, String errorCode, String errorDescription) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
}
