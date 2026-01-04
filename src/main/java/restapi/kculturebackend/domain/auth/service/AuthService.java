package restapi.kculturebackend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ConflictException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.common.exception.UnauthorizedException;
import restapi.kculturebackend.common.exception.ValidationException;
import restapi.kculturebackend.domain.auth.dto.*;
import restapi.kculturebackend.domain.auth.entity.RefreshToken;
import restapi.kculturebackend.domain.auth.repository.RefreshTokenRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.repository.UserRepository;
import restapi.kculturebackend.security.jwt.JwtTokenProvider;

/**
 * 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 사용자 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 사용자 정보 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getId().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Refresh Token 저장 (Redis)
        saveRefreshToken(user.getId().toString(), user.getEmail(), refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserInfo.from(user))
                .build();
    }

    /**
     * 회원가입
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 비밀번호 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "이미 사용 중인 이메일입니다.");
        }

        // 약관 동의 확인
        if (!request.getTermsAgreed() || !request.getPrivacyAgreed()) {
            throw new ValidationException("필수 약관에 동의해야 합니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getEmail().split("@")[0]) // 임시 이름
                .type(request.getType())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {} ({})", savedUser.getEmail(), savedUser.getType());

        return SignupResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .type(savedUser.getType())
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.deleteById(userId);
        log.info("User logged out: {}", userId);
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public AuthTokens refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // Redis에서 Refresh Token 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 Refresh Token입니다."));

        // 사용자 조회
        User user = userRepository.findByEmail(storedToken.getEmail())
                .orElseThrow(() -> new NotFoundException("사용자"));

        // 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getId().toString());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 기존 Refresh Token 삭제 및 새로운 Refresh Token 저장
        refreshTokenRepository.deleteByToken(refreshToken);
        saveRefreshToken(user.getId().toString(), user.getEmail(), newRefreshToken);

        return AuthTokens.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * 비밀번호 찾기 (이메일 발송)
     */
    @Transactional(readOnly = true)
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("사용자"));

        // TODO: 실제로는 이메일 발송 로직 구현 필요
        // 임시로 토큰 생성만 (실제로는 이메일로 전송)
        String resetToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getId().toString());
        log.info("Password reset token generated for: {} (Token: {})", user.getEmail(), resetToken);
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 비밀번호 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 검증
        if (!jwtTokenProvider.validateToken(request.getToken())) {
            throw new ValidationException("INVALID_TOKEN", "유효하지 않거나 만료된 토큰입니다.");
        }

        // 사용자 조회
        String email = jwtTokenProvider.getEmailFromToken(request.getToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("사용자"));

        // 비밀번호 변경 (User 엔티티에 메서드 추가 필요)
        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(user.getName())
                .type(user.getType())
                .profileImage(user.getProfileImage())
                .isActive(user.getIsActive())
                .build();

        userRepository.save(updatedUser);
        log.info("Password reset for user: {}", email);
    }

    /**
     * 계정 삭제
     */
    @Transactional
    public void deleteAccount(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException("사용자"));

        user.deactivate();
        userRepository.save(user);
        
        // Refresh Token 삭제
        refreshTokenRepository.deleteById(userId);
        
        log.info("User account deleted: {}", userId);
    }

    /**
     * Refresh Token 저장
     */
    private void saveRefreshToken(String userId, String email, String token) {
        RefreshToken refreshToken = RefreshToken.of(
                userId,
                email,
                token,
                604800L // 7일 (초 단위)
        );
        refreshTokenRepository.save(refreshToken);
    }
}

