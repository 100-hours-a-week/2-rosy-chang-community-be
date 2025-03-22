// UserServiceImpl.java
package kakao.community_backend.service;

import kakao.community_backend.dto.PasswordUpdateRequest;
import kakao.community_backend.dto.SignupRequest;
import kakao.community_backend.dto.TokenResponse;
import kakao.community_backend.dto.UserDto;
import kakao.community_backend.entity.User;
import kakao.community_backend.exception.DuplicateResourceException;
import kakao.community_backend.repository.UserRepository;
import kakao.community_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // 생성자 주입으로 변경

    @Override
    @Transactional
    public UserDto createUser(SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("email", "이미 사용 중인 이메일입니다");
        }

        // 닉네임 중복 확인
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new DuplicateResourceException("nickname", "이미 사용 중인 닉네임입니다");
        }

        // 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        // 사용자 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .profileImageUrl(request.getProfileImage())
                .isDeleted(false)
                .build();

        // 저장 및 DTO 변환 반환
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(String email, String password) {
        // 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        // 토큰 생성
        String token = jwtUtil.generateToken(user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // 토큰 응답 생성
        return TokenResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .token(token)
                .refreshToken(refreshToken)
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다");
        }

        // 새 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getPasswordCheck())) {
            throw new RuntimeException("새 비밀번호가 일치하지 않습니다");
        }

        // 비밀번호 업데이트
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDto updateProfile(Long userId, String nickname, MultipartFile profileImage) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 닉네임 변경 시 중복 확인
        if (nickname != null && !nickname.equals(user.getNickname())) {
            userRepository.findByNickname(nickname).ifPresent(u -> {
                throw new DuplicateResourceException("nickname", "이미 사용 중인 닉네임입니다");
            });
            user.setNickname(nickname);
        }

        // 프로필 이미지 업로드 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 실제 구현에서는 파일 저장 서비스를 사용하여 파일을 저장하고 URL을 반환받아야 함
                // 여기서는 간단하게 파일명만 저장한다고 가정
                String imageUrl = "/uploads/" + profileImage.getOriginalFilename();
                user.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 업로드에 실패했습니다", e);
            }
        }

        // 저장 및 DTO 변환 반환
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 논리적 삭제 처리
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return convertToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없습니다"));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다");
        }

        // 토큰에서 사용자 ID 추출
        Long userId = jwtUtil.extractUserId(refreshToken);

        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 새 토큰 생성
        String newToken = jwtUtil.generateToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        // 토큰 응답 생성
        return TokenResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    // 엔티티를 DTO로 변환하는 유틸리티 메서드
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}