// CommentService.java (인터페이스)
package kakao.community_backend.service;

import kakao.community_backend.dto.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentService {
    Page<CommentDto> getCommentsByPostId(Long postId, int page, int size);
    Long createComment(Long userId, Long postId, String content);
    void updateComment(Long userId, Long commentId, String content);
    void deleteComment(Long userId, Long commentId);
}