// PostService.java (인터페이스)
package kakao.community_backend.service;

import kakao.community_backend.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Page<PostDto> getAllPosts(int page, int size, String sort);
    PostDto getPostById(Long postId);
    Long createPost(Long userId, String title, String content, List<MultipartFile> images);
    void updatePost(Long userId, Long postId, String title, String content, List<MultipartFile> images, List<Long> deleteImageIds);
    void deletePost(Long userId, Long postId);
    boolean toggleLike(Long userId, Long postId);
    int getLikeCount(Long postId);
}