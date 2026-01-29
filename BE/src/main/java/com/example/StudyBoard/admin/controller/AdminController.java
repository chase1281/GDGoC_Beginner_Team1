package com.example.StudyBoard.admin.controller;

import com.example.StudyBoard.admin.service.AdminBoardService;
import com.example.StudyBoard.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminBoardService adminBoardService;
    private final AdminMemberService adminMemberService;

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("boardId") Long boardId){
        adminBoardService.delete(boardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") Long memberId){
        adminMemberService.delete(memberId);
        return ResponseEntity.noContent().build();
    }

}
