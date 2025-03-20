package kakao.community_backend.repository;

import kakao.community_backend.entity.Post;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 삭제되지 않은 게시글 중 ID로 찾기
    Optional<Post> findByPostIdAndIsDeletedFalse(Long postId);

    // 삭제되지 않은 모든 게시글 페이징 정리하여 찾기
    Page<Post> findByIsDeletedFalse(Pageable pageable);

    // 특정 사용자가 작성한 삭제되지 않은 게시글 찾기
    List<Post> findByUserUserIdAndIsDeletedFalse(Long userId);

    // 제목에 특정 키워드를 포함하는 삭제되지 않은 게시글 찾기
    Page<Post> findByTitleContainingAndIsDeletedFalse(String keyword, Pageable pageable);

    // JPQL을 사용한 인기 게시글 찾기 (좋아요 수 기준)
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.likeCount DESC")
    Page<Post> findPopularPosts(Pageable pageable);


}
