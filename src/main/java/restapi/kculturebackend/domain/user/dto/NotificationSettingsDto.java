package restapi.kculturebackend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.user.entity.NotificationSettings;

/**
 * 알림 설정 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettingsDto {
    private Boolean castingNotification;
    private Boolean messageNotification;
    private Boolean marketingNotification;

    public static NotificationSettingsDto from(NotificationSettings settings) {
        return NotificationSettingsDto.builder()
                .castingNotification(settings.getCastingNotification())
                .messageNotification(settings.getMessageNotification())
                .marketingNotification(settings.getMarketingNotification())
                .build();
    }

    public static NotificationSettingsDto createDefault() {
        return NotificationSettingsDto.builder()
                .castingNotification(true)
                .messageNotification(true)
                .marketingNotification(false)
                .build();
    }
}

