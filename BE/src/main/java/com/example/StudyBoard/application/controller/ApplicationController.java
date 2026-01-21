package com.example.StudyBoard.application.controller;

import com.example.StudyBoard.application.dto.response.ApplicationResponse;
import com.example.StudyBoard.application.service.ApplicationService;
import com.example.StudyBoard.auth.entity.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    //게시글 신청
    @PostMapping("/boards/{boardId}")
    public ResponseEntity<Void> apply(@PathVariable Long boardId, Authentication authentication) {

        Long memberId = (Long) authentication.getPrincipal();
        applicationService.apply(boardId, memberId);
        return ResponseEntity.ok().build();
    }

    //신청 취소 (신청자)
    @PostMapping("/{applicationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long applicationId,Authentication authentication) {

        Long memberId = (Long) authentication.getPrincipal();
        applicationService.cancel(applicationId, memberId);
        return ResponseEntity.ok().build();
    }

    //신청 승인 (게시글 작성자)
    @PostMapping("/{applicationId}/accept")
    public ResponseEntity<Void> accept(@PathVariable Long applicationId,Authentication authentication) {

        Long memberId = (Long) authentication.getPrincipal();
        applicationService.accept(applicationId, memberId);
        return ResponseEntity.ok().build();
    }

    //신청 거절 (게시글 작성자)
    @PostMapping("/{applicationId}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long applicationId,Authentication authentication) {

        Long memberId = (Long) authentication.getPrincipal();
        applicationService.reject(applicationId, memberId);
        return ResponseEntity.ok().build();
    }
}