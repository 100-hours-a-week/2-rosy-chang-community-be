// CommentController.java
package kakao.community_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kakao.community_backend.dto.ApiResponse;
import kakao.community_backend.dto.CommentCreateRequest;
import kakao.community_backend.dto.CommentDto;
import kakao.community_backend.dto.CommentUpdateRequest;
import kakao.community_backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<CommentDto> comments = commentService.getCommentsByPostId(postId, page - 1, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", comments.getContent());

        Map<String, Object> pageable = new HashMap<>();
        pageable.put("page", page);
        pageable.put("size", size);
        pageable.put("totalElements", comments.getTotalElements());
        pageable.put("totalPages", comments.getTotalPages());

        response.put("pageable", pageable);

        return ResponseEntity.ok(ApiResponse.success(200, "댓글 목록 조회 성공", response));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createComment(
            HttpServletRequest request,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest commentRequest) {

        Long userId = (Long) request.getAttribute("userId");
        Long commentId = commentService.createComment(userId, postId, commentRequest.getContent());

        Map<String, Long> result = new HashMap<>();
        result.put("commentId", commentId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "댓글 작성 성공", result));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> updateComment(
            HttpServletRequest request,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest commentRequest) {

        Long userId = (Long) request.getAttribute("userId");
        commentService.updateComment(userId, commentId, commentRequest.getContent());

        Map<String, Long> result = new HashMap<>();
        result.put("commentId", commentId);

        return ResponseEntity.ok(ApiResponse.success(200, "댓글 수정 성공", result));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            HttpServletRequest request,
            @PathVariable Long commentId) {

        Long userId = (Long) request.getAttribute("userId");
        commentService.deleteComment(userId, commentId);

        return ResponseEntity.ok(ApiResponse.success(200, "댓글 삭제 성공", null));
    }
}