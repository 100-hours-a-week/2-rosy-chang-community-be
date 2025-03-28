package kakao.community_backend.repository;

import kakao.community_backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 삭제되지 않은 댓글 중 ID로 찾기
    Optional<Comment> findByCommentIdAndIsDeletedFalse(Long commentId);

    // 특정 게시글의 삭제되지 않은 댓글 찾기 (페이징)
    Page<Comment> findByPostPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    // 특정 사용자가 작성한 삭제되지 않은 댓글 찾기
    List<Comment> findByUserUserIdAndIsDeletedFalse(Long userId);
}