package restapi.kculturebackend.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import restapi.kculturebackend.common.exception.ConflictException;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.common.exception.UnauthorizedException;
import restapi.kculturebackend.common.exception.ValidationException;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.agency.entity.AgencyProfile;
import restapi.kculturebackend.domain.agency.repository.AgencyProfileRepository;
import restapi.kculturebackend.domain.auth.dto.AuthTokens;
import restapi.kculturebackend.domain.auth.dto.ForgotPasswordRequest;
import restapi.kculturebackend.domain.auth.dto.LoginRequest;
import restapi.kculturebackend.domain.auth.dto.LoginResponse;
import restapi.kculturebackend.domain.auth.dto.ResetPasswordRequest;
import restapi.kculturebackend.domain.auth.dto.SignupRequest;
import restapi.kculturebackend.domain.auth.dto.SignupResponse;
import restapi.kculturebackend.domain.auth.dto.UserInfo;
import restapi.kculturebackend.domain.auth.entity.RefreshToken;
import restapi.kculturebackend.domain.auth.repository.RefreshTokenRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserProfile;
import restapi.kculturebackend.domain.user.entity.UserType;
import restapi.kculturebackend.domain.user.repository.UserProfileRepository;
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
    private final UserProfileRepository userProfileRepository;
    private final ActorProfileRepository actorProfileRepository;
    private final AgencyProfileRepository agencyProfileRepository;
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
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS));

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
            throw new ValidationException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 약관 동의 확인
        if (!request.getTermsAgreed() || !request.getPrivacyAgreed()) {
            throw new ValidationException(ErrorCode.TERMS_NOT_AGREED);
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
        
        // UserProfile 자동 생성 (공통 프로필)
        UserProfile profile = UserProfile.createDefault(savedUser);
        userProfileRepository.save(profile);
        
        // 사용자 타입에 따라 전용 프로필 생성
        if (savedUser.getType() == UserType.ACTOR) {
            ActorProfile actorProfile = ActorProfile.createDefault(savedUser);
            actorProfileRepository.save(actorProfile);
            log.info("Actor profile created for user: {}", savedUser.getEmail());
        } else if (savedUser.getType() == UserType.AGENCY) {
            AgencyProfile agencyProfile = AgencyProfile.createDefault(savedUser);
            agencyProfileRepository.save(agencyProfile);
            log.info("Agency profile created for user: {}", savedUser.getEmail());
        }
        
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
     * 토큰 갱신 (쿠키에서 refreshToken을 직접 받음)
     */
    @Transactional
    public AuthTokens refreshToken(String refreshToken) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에서 Refresh Token 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 사용자 조회
        User user = userRepository.findByEmail(storedToken.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

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
            throw new ValidationException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 토큰 검증
        if (!jwtTokenProvider.validateToken(request.getToken())) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        // 사용자 조회
        String email = jwtTokenProvider.getEmailFromToken(request.getToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("Password reset for user: {}", email);
    }

    /**
     * 계정 삭제
     */
    @Transactional
    public void deleteAccount(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

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

