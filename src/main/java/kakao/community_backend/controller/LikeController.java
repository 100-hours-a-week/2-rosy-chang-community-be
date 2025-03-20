package kakao.community_backend.controller;

import kakao.community_backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkLikeStatus(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        boolean isLiked = likeService.isLikedByUser(postId, userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", isLiked);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        boolean isLiked = likeService.toggleLike(postId, userId);
        int likeCount = likeService.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getLikeCount(@RequestParam Long postId) {
        int likeCount = likeService.getLikeCount(postId);
        Map<String, Integer> response = new HashMap<>();
        response.put("likeCount", likeCount);
        return ResponseEntity.ok(response);
    }
}