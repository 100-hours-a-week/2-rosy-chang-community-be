// UserService.java (인터페이스)
package kakao.community_backend.service;

import kakao.community_backend.dto.PasswordUpdateRequest;
import kakao.community_backend.dto.SignupRequest;
import kakao.community_backend.dto.TokenResponse;
import kakao.community_backend.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDto createUser(SignupRequest request);
    TokenResponse login(String email, String password);
    void updatePassword(Long userId, PasswordUpdateRequest request);
    UserDto updateProfile(Long userId, String nickname, MultipartFile profileImage);
    void deleteUser(Long userId);
    UserDto getUserById(Long userId);
    UserDto getUserByEmail(String email);
    TokenResponse refreshToken(String refreshToken);
}