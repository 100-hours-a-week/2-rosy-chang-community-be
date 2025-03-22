// CommentUpdateRequest.java
package kakao.community_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentUpdateRequest {
    @NotBlank(message = "댓글 내용은 필수 항목입니다")
    private String content;
}