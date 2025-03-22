// CommentServiceImpl.java
package kakao.community_backend.service;

import kakao.community_backend.dto.CommentDto;
import kakao.community_backend.entity.Comment;
import kakao.community_backend.entity.Post;
import kakao.community_backend.entity.User;
import kakao.community_backend.repository.CommentRepository;
import kakao.community_backend.repository.PostRepository;
import kakao.community_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByPostId(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findByPostPostIdAndIsDeletedFalse(postId, pageable);
        return comments.map(this::convertToDto);
    }

    @Override
    @Transactional
    public Long createComment(Long userId, Long postId, String content) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 게시글 조회
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        // 댓글 생성
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .isDeleted(false)
                .build();

        // 저장
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getCommentId();
    }

    @Override
    @Transactional
    public void updateComment(Long userId, Long commentId, String content) {
        // 댓글 조회
        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        // 권한 확인
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("해당 댓글을 수정할 권한이 없습니다");
        }

        // 댓글 내용 업데이트
        comment.setContent(content);

        // 저장
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        // 댓글 조회
        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        // 권한 확인
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("해당 댓글을 삭제할 권한이 없습니다");
        }

        // 논리적 삭제 처리
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    // 엔티티를 DTO로 변환하는 유틸리티 메서드
    private CommentDto convertToDto(Comment comment) {
        return CommentDto.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .authorId(comment.getUser().getUserId())
                .authorNickname(comment.getUser().getNickname())
                .authorProfileImageUrl(comment.getUser().getProfileImageUrl())
                .postId(comment.getPost().getPostId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}