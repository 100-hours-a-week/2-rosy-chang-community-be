// PostServiceImpl.java
package kakao.community_backend.service;

import kakao.community_backend.dto.PostDto;
import kakao.community_backend.entity.Like;
import kakao.community_backend.entity.Post;
import kakao.community_backend.entity.User;
import kakao.community_backend.repository.LikeRepository;
import kakao.community_backend.repository.PostRepository;
import kakao.community_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> getAllPosts(int page, int size, String sort) {
        Pageable pageable;

        if ("popular".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("likeCount").descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        }

        Page<Post> posts = postRepository.findByIsDeletedFalse(pageable);
        return posts.map(this::convertToDto);
    }

    @Override
    @Transactional
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        // 조회수 증가
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        return convertToDto(post);
    }

    @Override
    @Transactional
    public Long createPost(Long userId, String title, String content, List<MultipartFile> images) {
        if (userId == null) {
            throw new IllegalArgumentException("The given id must not be null!!!!");
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 게시글 생성
        Post post = Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .viewCount(0)
                .likeCount(0)
                .isDeleted(false)
                .build();

        // 이미지 처리 (첫 번째 이미지만 대표 이미지로 설정)
        if (images != null && !images.isEmpty()) {
            try {
                // 실제 구현에서는 파일 저장 서비스를 사용하여 파일을 저장하고 URL을 반환받아야 함
                // 여기서는 간단하게 첫 번째 파일명만 저장한다고 가정
                String imageUrl = "/uploads/" + images.get(0).getOriginalFilename();
                post.setContentImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드에 실패했습니다", e);
            }
        }

        // 저장
        Post savedPost = postRepository.save(post);
        return savedPost.getPostId();
    }

    @Override
    @Transactional
    public void updatePost(Long userId, Long postId, String title, String content, List<MultipartFile> images, List<Long> deleteImageIds) {
        // 게시글 조회
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        // 권한 확인
        if (!post.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("해당 게시글을 수정할 권한이 없습니다");
        }

        // 게시글 정보 업데이트
        post.setTitle(title);
        post.setContent(content);

        // 이미지 처리 (기존 이미지 삭제 및 새 이미지 추가)
        if (images != null && !images.isEmpty()) {
            try {
                // 실제 구현에서는 파일 저장 서비스를 사용하여 파일을 저장하고 URL을 반환받아야 함
                // 여기서는 간단하게 첫 번째 파일명만 저장한다고 가정
                String imageUrl = "/uploads/" + images.get(0).getOriginalFilename();
                post.setContentImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드에 실패했습니다", e);
            }
        }

        // 저장
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        // 게시글 조회
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        // 권한 확인
        if (!post.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("해당 게시글을 삭제할 권한이 없습니다");
        }

        // 논리적 삭제 처리
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long postId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 게시글 조회
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        // 좋아요 상태 확인
        Optional<Like> existingLike = likeRepository.findByUserUserIdAndPostPostId(userId, postId);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return false;
        } else {
            // 좋아요 추가
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

    @Override
    @Transactional(readOnly = true)
    public int getLikeCount(Long postId) {
        return likeRepository.countByPostPostId(postId);
    }

    // 엔티티를 DTO로 변환하는 유틸리티 메서드
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