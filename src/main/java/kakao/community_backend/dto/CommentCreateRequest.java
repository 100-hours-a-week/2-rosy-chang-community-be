// CommentCreateRequest.java
package kakao.community_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateRequest {
    @NotBlank(message = "댓글 내용은 필수 항목입니다")
    private String content;
}