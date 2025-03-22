// PasswordUpdateRequest.java
package kakao.community_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateRequest {
    @NotBlank(message = "현재 비밀번호를 입력해주세요")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하이어야 합니다")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인을 입력해주세요")
    private String passwordCheck;
}
