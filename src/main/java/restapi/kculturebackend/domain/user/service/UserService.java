package restapi.kculturebackend.domain.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import restapi.kculturebackend.common.exception.BusinessException;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.user.dto.ChangePasswordRequest;
import restapi.kculturebackend.domain.user.dto.NotificationSettingsDto;
import restapi.kculturebackend.domain.user.dto.UpdateProfileRequest;
import restapi.kculturebackend.domain.user.dto.UserProfileResponse;
import restapi.kculturebackend.domain.user.entity.NotificationSettings;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserProfile;
import restapi.kculturebackend.domain.user.repository.UserProfileRepository;
import restapi.kculturebackend.domain.user.repository.UserRepository;

/**
 * 사용자 프로필 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 내 정보 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);

        return UserProfileResponse.from(user, profile);
    }

    /**
     * 프로필 수정
     */
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // User 이름 업데이트
        if (request.getName() != null) {
            user.updateProfile(request.getName(), user.getProfileImage());
            userRepository.save(user);
        }

        // UserProfile 조회 또는 생성
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.createDefault(user);
                    return userProfileRepository.save(newProfile);
                });

        // 프로필 정보 업데이트
        profile.updateProfile(
                request.getPosition(),
                request.getAgency(),
                request.getPhone(),
                request.getBio(),
                request.getFee(),
                request.getHeight(),
                request.getWeight()
        );

        userProfileRepository.save(profile);
        log.info("Profile updated for user: {}", userId);

        return UserProfileResponse.from(user, profile);
    }

    /**
     * 알림 설정 조회
     */
    @Transactional(readOnly = true)
    public NotificationSettingsDto getNotificationSettings(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_PROFILE_NOT_FOUND));

        NotificationSettings settings = profile.getNotificationSettings();
        if (settings == null) {
            return NotificationSettingsDto.createDefault();
        }

        return NotificationSettingsDto.from(settings);
    }

    /**
     * 알림 설정 수정
     */
    @Transactional
    public NotificationSettingsDto updateNotificationSettings(UUID userId, NotificationSettingsDto request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
                    UserProfile newProfile = UserProfile.createDefault(user);
                    return userProfileRepository.save(newProfile);
                });

        NotificationSettings settings = profile.getNotificationSettings();
        if (settings == null) {
            settings = NotificationSettings.createDefault(profile);
        }

        settings.updateSettings(
                request.getCastingNotification(),
                request.getMessageNotification(),
                request.getMarketingNotification()
        );

        userProfileRepository.save(profile);
        log.info("Notification settings updated for user: {}", userId);

        return NotificationSettingsDto.from(settings);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 새 비밀번호가 현재 비밀번호와 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.SAME_PASSWORD);
        }

        // 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }
}

