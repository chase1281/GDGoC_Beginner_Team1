package com.example.StudyBoard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequest(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일: 유효한 이메일 주소를 입력해주세요.")
        String email,

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(min = 1, max = 20, message = "이름은 1자 이상, 20자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, max = 60, message = "비밀번호는 8자 이상, 60자 이하로 입력해주세요.")
        String password
) {
}
