package kakao.community_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kakao.community_backend.dto.*;
import kakao.community_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponse<UserDto>> signup(@Valid @RequestBody SignupRequest request) {
        UserDto userDto = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "회원가입 성공", userDto));
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(200, "로그인 성공", tokenResponse));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        // 실제로는 토큰 블랙리스트 처리 등이 필요할 수 있음
        return ResponseEntity.ok(ApiResponse.success(200, "로그아웃 성공", null));
    }

    @PutMapping("/users/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            HttpServletRequest request,
            @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, passwordUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "비밀번호 변경 성공", null));
    }

    @PutMapping(value = "/users/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            HttpServletRequest request,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) MultipartFile profileImage) {
        Long userId = (Long) request.getAttribute("userId");
        UserDto updatedUser = userService.updateProfile(userId, nickname, profileImage);
        return ResponseEntity.ok(ApiResponse.success(200, "회원정보 수정 성공", updatedUser));
    }

    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(200, "회원탈퇴 성공", null));
    }

    @GetMapping("/users/profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(200, "내 정보 조회 성공", userDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "리프레시 토큰은 필수 항목입니다"));
        }

        TokenResponse newTokens = userService.refreshToken(refreshToken);

        return ResponseEntity.ok(ApiResponse.success(200, "토큰 갱신 성공", newTokens));
    }
}