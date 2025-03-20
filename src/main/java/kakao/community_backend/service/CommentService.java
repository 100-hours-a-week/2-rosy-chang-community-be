package kakao.community_backend.service;

import kakao.community_backend.dto.CommentDto;
import kakao.community_backend.entity.Comment;
import kakao.community_backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 특정 게시글의 댓글 조회
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByPostId(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findByPostPostIdAndIsDeletedFalse(postId, pageable);
        return comments.map(this::convertToDto);
    }

    // Entity를 DTO로 변환
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