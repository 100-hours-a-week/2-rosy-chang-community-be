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
public class CommentDto {
    private Long commentId;
    private String content;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImageUrl;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}