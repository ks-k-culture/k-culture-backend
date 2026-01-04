package restapi.kculturebackend.domain.actor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.actor.entity.Showreel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 쇼릴 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowreelResponse {
    private UUID id;
    private UUID actorId;
    private String actorName;
    private String title;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer duration;
    private String durationFormatted;
    private String workTitle;
    private String genre;
    private String description;
    private List<String> tags;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShowreelResponse from(Showreel showreel) {
        return ShowreelResponse.builder()
                .id(showreel.getId())
                .actorId(showreel.getActor().getUserId())
                .actorName(showreel.getActor().getStageName() != null 
                        ? showreel.getActor().getStageName() 
                        : showreel.getActor().getUser().getName())
                .title(showreel.getTitle())
                .videoUrl(showreel.getVideoUrl())
                .thumbnailUrl(showreel.getThumbnailUrl())
                .duration(showreel.getDuration())
                .durationFormatted(formatDuration(showreel.getDuration()))
                .workTitle(showreel.getWorkTitle())
                .genre(showreel.getGenre())
                .description(showreel.getDescription())
                .tags(showreel.getTags())
                .viewCount(showreel.getViewCount())
                .createdAt(showreel.getCreatedAt())
                .updatedAt(showreel.getUpdatedAt())
                .build();
    }

    private static String formatDuration(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return null;
        }
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}

