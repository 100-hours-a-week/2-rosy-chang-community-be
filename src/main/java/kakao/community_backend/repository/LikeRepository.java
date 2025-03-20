package kakao.community_backend.repository;

import kakao.community_backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<Like> findByUserUserIdAndPostPostId(Long userId, Long postId);

    // 특정 게시글의 좋아요 수 계산
    int countByPostPostId(Long postId);

    // 특정 사용자가 좋아요를 누른 게시글 목록
    List<Like> findByUserUserId(Long userId);
}
