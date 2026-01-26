package com.example.StudyBoard.board.controller;

import ch.qos.logback.core.model.processor.PhaseIndicator;
import com.example.StudyBoard.auth.entity.CustomMemberDetails;
import com.example.StudyBoard.board.dto.request.BoardCreateRequest;
import com.example.StudyBoard.board.dto.request.BoardEditRequest;
import com.example.StudyBoard.board.dto.response.BoardResponse;
import com.example.StudyBoard.board.service.BoardService;
import com.example.StudyBoard.member.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //게시글 생성
    @PostMapping("/create")
    public ResponseEntity<BoardResponse> createBoard(
            @RequestBody @Valid BoardCreateRequest request,
            Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        BoardResponse response = boardService.create(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //단건 조회
    @GetMapping("/id/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.get(boardId));
    }

    //목록 조회
    @GetMapping("/recruiting")
    public ResponseEntity<List<BoardResponse>> getRecruitingBoards() {
        return ResponseEntity.ok(boardService.getRecruitingBoards());
    }

    //삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> delete(@PathVariable Long boardId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        boardService.delete(boardId, memberId);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    //수정
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponse> edit(@PathVariable Long boardId, Authentication authentication, @RequestBody @Valid BoardEditRequest request) {
        Long memberId = (Long) authentication.getPrincipal();
        BoardResponse response = boardService.edit(boardId, memberId, request);
        return ResponseEntity.ok(response);
    }
}