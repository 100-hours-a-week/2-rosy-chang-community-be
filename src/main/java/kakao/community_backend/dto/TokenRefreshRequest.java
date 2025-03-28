package kakao.community_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {
    @NotBlank(message = "리프레시 토큰은 필수 항목입니다")
    private String refreshToken;
}