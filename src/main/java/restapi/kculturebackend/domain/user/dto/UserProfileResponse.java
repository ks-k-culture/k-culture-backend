package restapi.kculturebackend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserProfile;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.UUID;

/**
 * 사용자 프로필 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private UUID id;
    private String email;
    private String name;
    private UserType type;
    private String profileImage;
    private String position;
    private String agency;
    private String phone;
    private String bio;
    private String fee;
    private Integer height;
    private Integer weight;
    private NotificationSettingsDto settings;

    public static UserProfileResponse from(User user, UserProfile profile) {
        NotificationSettingsDto settingsDto = profile != null && profile.getNotificationSettings() != null
                ? NotificationSettingsDto.from(profile.getNotificationSettings())
                : NotificationSettingsDto.createDefault();

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .type(user.getType())
                .profileImage(user.getProfileImage())
                .position(profile != null ? profile.getPosition() : null)
                .agency(profile != null ? profile.getAgency() : null)
                .phone(profile != null ? profile.getPhone() : null)
                .bio(profile != null ? profile.getBio() : null)
                .fee(profile != null ? profile.getFee() : null)
                .height(profile != null ? profile.getHeight() : null)
                .weight(profile != null ? profile.getWeight() : null)
                .settings(settingsDto)
                .build();
    }
}

