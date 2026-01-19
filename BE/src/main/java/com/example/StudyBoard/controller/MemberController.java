package com.example.StudyBoard.controller;

import com.example.StudyBoard.dto.request.MemberLoginRequest;
import com.example.StudyBoard.dto.request.MemberRegisterRequest;
import com.example.StudyBoard.dto.response.MemberInfoResponse;
import com.example.StudyBoard.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> registerMember(@RequestBody @Valid MemberRegisterRequest request){
        memberService.registerMember(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("회원가입이 성공하였습니다.");
    }
}
