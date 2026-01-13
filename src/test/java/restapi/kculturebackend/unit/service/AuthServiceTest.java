package restapi.kculturebackend.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import restapi.kculturebackend.common.exception.ConflictException;
import restapi.kculturebackend.common.exception.UnauthorizedException;
import restapi.kculturebackend.common.exception.ValidationException;
import restapi.kculturebackend.domain.actor.repository.ActorProfileRepository;
import restapi.kculturebackend.domain.agency.repository.AgencyProfileRepository;
import restapi.kculturebackend.domain.auth.dto.AuthTokens;
import restapi.kculturebackend.domain.auth.dto.LoginRequest;
import restapi.kculturebackend.domain.auth.dto.LoginResponse;
import restapi.kculturebackend.domain.auth.dto.SignupRequest;
import restapi.kculturebackend.domain.auth.dto.SignupResponse;
import restapi.kculturebackend.domain.auth.entity.RefreshToken;
import restapi.kculturebackend.domain.auth.repository.RefreshTokenRepository;
import restapi.kculturebackend.domain.auth.service.AuthService;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;
import restapi.kculturebackend.domain.user.repository.UserProfileRepository;
import restapi.kculturebackend.domain.user.repository.UserRepository;
import restapi.kculturebackend.security.jwt.JwtTokenProvider;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserProfileRepository userProfileRepository;
    
    @Mock
    private ActorProfileRepository actorProfileRepository;
    
    @Mock
    private AgencyProfileRepository agencyProfileRepository;
    
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest(
                "test@example.com",
                "password123",
                "password123",
                UserType.ACTOR,
                true,
                true,
                false
        );

        loginRequest = new LoginRequest("test@example.com", "password123");

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encodedPassword")
                .name("test")
                .type(UserType.ACTOR)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // ID 설정을 위한 reflection 또는 새 User 반환
            return User.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .name(user.getName())
                    .type(user.getType())
                    .isActive(user.getIsActive())
                    .build();
        });
        when(userProfileRepository.save(any())).thenReturn(null);
        when(actorProfileRepository.save(any())).thenReturn(null);

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getType()).isEqualTo(UserType.ACTOR);
        
        verify(userRepository).save(any(User.class));
        verify(userProfileRepository).save(any());
        verify(actorProfileRepository).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_Fail_EmailExists() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(ConflictException.class);
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void signup_Fail_PasswordMismatch() {
        // given
        SignupRequest mismatchRequest = new SignupRequest(
                "test@example.com",
                "password123",
                "differentPassword",
                UserType.ACTOR,
                true,
                true,
                false
        );

        // when & then
        assertThatThrownBy(() -> authService.signup(mismatchRequest))
                .isInstanceOf(ValidationException.class);
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 약관 미동의")
    void signup_Fail_TermsNotAgreed() {
        // given
        SignupRequest noTermsRequest = new SignupRequest(
                "test@example.com",
                "password123",
                "password123",
                UserType.ACTOR,
                false,  // termsAgreed = false
                true,
                false
        );

        // when & then
        assertThatThrownBy(() -> authService.signup(noTermsRequest))
                .isInstanceOf(ValidationException.class);
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser, null));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createAccessToken(anyString(), any())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any())).thenReturn(null);

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // given
        String userId = UUID.randomUUID().toString();
        doNothing().when(refreshTokenRepository).deleteById(userId);

        // when
        authService.logout(userId);

        // then
        verify(refreshTokenRepository).deleteById(userId);
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_Success() {
        // given
        String oldRefreshToken = "oldRefreshToken";
        RefreshToken storedToken = RefreshToken.of(
                testUser.getId().toString(),
                testUser.getEmail(),
                oldRefreshToken,
                604800L
        );

        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(refreshTokenRepository.findByToken(oldRefreshToken)).thenReturn(Optional.of(storedToken));
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createAccessToken(anyString(), any())).thenReturn("newAccessToken");
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn("newRefreshToken");
        doNothing().when(refreshTokenRepository).deleteByToken(oldRefreshToken);
        when(refreshTokenRepository.save(any())).thenReturn(null);

        // when
        AuthTokens tokens = authService.refreshToken(oldRefreshToken);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(tokens.getRefreshToken()).isEqualTo("newRefreshToken");
        
        verify(refreshTokenRepository).deleteByToken(oldRefreshToken);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 토큰")
    void refreshToken_Fail_InvalidToken() {
        // given
        String invalidToken = "invalidToken";
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(invalidToken))
                .isInstanceOf(UnauthorizedException.class);
        
        verify(refreshTokenRepository, never()).findByToken(anyString());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 저장된 토큰 없음")
    void refreshToken_Fail_TokenNotFound() {
        // given
        String refreshToken = "notFoundToken";
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(UnauthorizedException.class);
        
        verify(userRepository, never()).findByEmail(anyString());
    }
}

