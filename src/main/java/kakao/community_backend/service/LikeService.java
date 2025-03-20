package kakao.community_backend.service;

import kakao.community_backend.entity.Like;
import kakao.community_backend.entity.Post;
import kakao.community_backend.entity.User;
import kakao.community_backend.repository.LikeRepository;
import kakao.community_backend.repository.PostRepository;
import kakao.community_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 좋아요 상태 확인
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, Long userId) {
        return likeRepository.findByUserUserIdAndPostPostId(userId, postId).isPresent();
    }

    // 좋아요 토글
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        Optional<Like> existingLike = likeRepository.findByUserUserIdAndPostPostId(userId, postId);

        if (existingLike.isPresent()) {
            // 좋아요가 이미 있으면 삭제
            likeRepository.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return false;
        } else {
            // 좋아요가 없으면 추가
            Like like = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            return true;
        }
    }

    // 게시글의 좋아요 수 조회
    @Transactional(readOnly = true)
    public int getLikeCount(Long postId) {
        return likeRepository.countByPostPostId(postId);
    }
}