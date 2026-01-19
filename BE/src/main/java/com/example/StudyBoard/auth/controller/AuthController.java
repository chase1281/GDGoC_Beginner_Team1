package com.example.StudyBoard.auth.controller;

import com.example.StudyBoard.auth.dto.LoginMemberRequest;
import com.example.StudyBoard.auth.dto.LoginRequest;
import com.example.StudyBoard.auth.dto.LoginSuccessResponse;
import com.example.StudyBoard.auth.dto.RefreshTokenRequest;
import com.example.StudyBoard.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@Valid @RequestBody final LoginRequest loginRequest){
        LoginSuccessResponse loginSuccessResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginSuccessResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginSuccessResponse> refresh(@Valid @RequestBody final RefreshTokenRequest refreshTokenRequest){
        LoginSuccessResponse loginSuccessResponse = authService.refreshAuthToken(refreshTokenRequest);
        return ResponseEntity.ok(loginSuccessResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginSuccessResponse> logout(@AuthenticationPrincipal final Object principal){
        Long memberId = Long.parseLong(principal.toString());
        LoginSuccessResponse loginSuccessResponse = authService.logout(new LoginMemberRequest(memberId));
        return ResponseEntity.ok(loginSuccessResponse);
    }
}
