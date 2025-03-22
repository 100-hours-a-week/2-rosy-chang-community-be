// PostController.java
package kakao.community_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kakao.community_backend.dto.ApiResponse;
//import kakao.community_backend.dto.PostCreateRequest;
import kakao.community_backend.dto.PostDto;
//import kakao.community_backend.dto.PostUpdateRequest;
import kakao.community_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        Page<PostDto> posts = postService.getAllPosts(page - 1, size, sort);

        Map<String, Object> response = new HashMap<>();
        response.put("content", posts.getContent());

        Map<String, Object> pageable = new HashMap<>();
        pageable.put("page", page);
        pageable.put("size", size);
        pageable.put("totalElements", posts.getTotalElements());
        pageable.put("totalPages", posts.getTotalPages());

        response.put("pageable", pageable);

        return ResponseEntity.ok(ApiResponse.success(200, "게시글 목록 조회 성공", response));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostDto>> getPostById(@PathVariable Long postId) {
        PostDto post = postService.getPostById(postId);
        return ResponseEntity.ok(ApiResponse.success(200, "게시글 상세 조회 성공", post));
    }

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Long>>> createPost(
            HttpServletRequest request,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> images) {

        Long userId = (Long) request.getAttribute("userId");
        Long postId = postService.createPost(userId, title, content, images);

        Map<String, Long> result = new HashMap<>();
        result.put("postId", postId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "게시글 작성 성공", result));
    }

    @PutMapping(value = "/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Long>>> updatePost(
            HttpServletRequest request,
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) List<Long> deleteImageIds) {

        Long userId = (Long) request.getAttribute("userId");
        postService.updatePost(userId, postId, title, content, images, deleteImageIds);

        Map<String, Long> result = new HashMap<>();
        result.put("postId", postId);

        return ResponseEntity.ok(ApiResponse.success(200, "게시글 수정 성공", result));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            HttpServletRequest request,
            @PathVariable Long postId) {

        Long userId = (Long) request.getAttribute("userId");
        postService.deletePost(userId, postId);

        return ResponseEntity.ok(ApiResponse.success(200, "게시글 삭제 성공", null));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> likePost(
            HttpServletRequest request,
            @PathVariable Long postId) {

        Long userId = (Long) request.getAttribute("userId");
        boolean liked = postService.toggleLike(userId, postId);
        int likeCount = postService.getLikeCount(postId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);

        return ResponseEntity.ok(ApiResponse.success(200, liked ? "좋아요 성공" : "좋아요 취소 성공", result));
    }
}