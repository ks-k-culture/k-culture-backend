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
 * 공지사항 요약 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeSummaryResponse {
    private UUID id;
    private NoticeType type;
    private String typeDisplayName;
    private String title;
    private LocalDateTime createdAt;

    public static NoticeSummaryResponse from(Notice notice) {
        return NoticeSummaryResponse.builder()
                .id(notice.getId())
                .type(notice.getType())
                .typeDisplayName(notice.getType().getDisplayName())
                .title(notice.getTitle())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
