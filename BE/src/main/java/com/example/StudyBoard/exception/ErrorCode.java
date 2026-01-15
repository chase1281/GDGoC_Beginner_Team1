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
    DUPLICATED_NAME(HttpStatus.BAD_REQUEST, "-101", "이미 존재하는 닉네임입니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "-102", "비밀번호가 일치하지 않습니다."),
    NULL_MEMBER(HttpStatus.BAD_REQUEST, "-103", "존재하지 않는 회원입니다."),
    DELETED_MEMBER(HttpStatus.BAD_REQUEST, "-104", "탈퇴한 회원입니다."),

    //인증/권한
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "-200", "로그인이 필요합니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "-201", "접근 권한이 없습니다."),
    NOT_WRITER(HttpStatus.FORBIDDEN, "-202", "작성자만 수정/삭제할 수 있습니다."),

    //게시글
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "-300", "존재하지 않는 게시글입니다."),
    RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "-301", "모집이 마감된 게시글입니다."),

    //신청
    DUPLICATED_APPLIED(HttpStatus.BAD_REQUEST, "-400", "이미 신청한 게시글입니다."),
    SELF_APPLICATION(HttpStatus.BAD_REQUEST, "-401", "본인의 게시글에는 신청할 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "-402", "신청 내역을 찾을 수 없습니다."),
    CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "-403", "모집 정원이 초과되었습니다.")
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
