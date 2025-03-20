package kakao.community_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    private String contentImageUrl;
    private int viewCount;
    private int likeCount;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}