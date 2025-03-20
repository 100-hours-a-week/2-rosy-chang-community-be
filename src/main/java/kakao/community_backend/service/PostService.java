package kakao.community_backend.service;

import kakao.community_backend.dto.PostDto;
import kakao.community_backend.entity.Post;
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
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 모든 게시글 페이징 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByIsDeletedFalse(pageable);
        return posts.map(this::convertToDto);
    }

    // 인기 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPopularPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findPopularPosts(pageable);
        return posts.map(this::convertToDto);
    }

    // 특정 게시글 조회
    @Transactional
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        // 조회수 증가
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        return convertToDto(post);
    }

    // 게시글 검색
    @Transactional(readOnly = true)
    public Page<PostDto> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);
        return posts.map(this::convertToDto);
    }

    // Entity를 DTO로 변환
    private PostDto convertToDto(Post post) {
        return PostDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .contentImageUrl(post.getContentImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .authorId(post.getUser().getUserId())
                .authorNickname(post.getUser().getNickname())
                .authorProfileImageUrl(post.getUser().getProfileImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}