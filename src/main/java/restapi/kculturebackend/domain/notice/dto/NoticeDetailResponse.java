package restapi.kculturebackend.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.notice.entity.Notice;
import restapi.kculturebackend.domain.notice.entity.NoticeType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 공지사항 상세 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailResponse {
    private UUID id;
    private NoticeType type;
    private String typeDisplayName;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoticeDetailResponse from(Notice notice) {
        return NoticeDetailResponse.builder()
                .id(notice.getId())
                .type(notice.getType())
                .typeDisplayName(notice.getType().getDisplayName())
                .title(notice.getTitle())
                .content(notice.getContent())
                .views(notice.getViews())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
