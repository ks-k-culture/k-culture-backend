package restapi.kculturebackend.domain.dashboard.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.dashboard.entity.Activity;

/**
 * 활동 내역 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponse {
    private UUID id;
    private String type;
    private String message;
    private UUID relatedUserId;
    private String relatedUserName;
    private UUID relatedEntityId;
    private LocalDateTime createdAt;

    public static ActivityResponse from(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .type(activity.getType().getCode())
                .message(activity.getMessage())
                .relatedUserId(activity.getRelatedUserId())
                .relatedUserName(activity.getRelatedUserName())
                .relatedEntityId(activity.getRelatedEntityId())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
