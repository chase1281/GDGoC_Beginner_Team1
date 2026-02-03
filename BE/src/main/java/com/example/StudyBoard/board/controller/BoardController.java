package com.example.StudyBoard.board.controller;

import com.example.StudyBoard.board.dto.request.BoardCreateRequest;
import com.example.StudyBoard.board.dto.request.BoardEditRequest;
import com.example.StudyBoard.board.dto.response.BoardResponse;
import com.example.StudyBoard.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

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

    //모집중인 목록 조회
    @GetMapping("/recruiting")
    public ResponseEntity<Page<BoardResponse>> getRecruitingBoards(
            @PageableDefault(size=10, sort = "boardId", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(boardService.getRecruitingBoards(pageable));
    }

    //모집 완료 목록 조회
    @GetMapping("/closed")
    public ResponseEntity<Page<BoardResponse>> getClosedBoards(
            @PageableDefault(size=10, sort = "boardId", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(boardService.getClosedBoards(pageable));
    }

    //전체 목록 조회
    @GetMapping("/all")
    public ResponseEntity<Page<BoardResponse>> getAllBoards(
            @PageableDefault(size=10, sort = "boardId", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(boardService.getAllBoards(pageable));
    }

    //내가 만든 스터디 목록 조회
    @GetMapping("/my")
    public ResponseEntity<Page<BoardResponse>> getMyBoards(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "boardId", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(boardService.getMyBoards(memberId, pageable));
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