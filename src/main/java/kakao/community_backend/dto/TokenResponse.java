// TokenResponse.java
package kakao.community_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String token;
    private String refreshToken;
    private String profileImageUrl;
}
