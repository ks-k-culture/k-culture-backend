package restapi.kculturebackend.domain.actor.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;

/**
 * 배우 상세 조회 응답 DTO
 */
@Getter
@Builder
public class ActorDetailResponse {
    private UUID id;
    private String email;
    private String name;
    private String stageName;
    private String profileImage;
    private Integer birthYear;
    private String introduction;
    private String nationality;
    private Integer height;
    private Integer weight;
    private List<String> skills;
    private List<String> languages;
    private String agency;
    private Boolean isProfileComplete;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ActorDetailResponse from(ActorProfile actor) {
        return from(actor, 0L, 0L);
    }

    public static ActorDetailResponse from(ActorProfile actor, Long viewCount, Long likeCount) {
        List<String> skillsCopy = actor.getSkills() != null ? new ArrayList<>(actor.getSkills()) : new ArrayList<>();
        List<String> languagesCopy = actor.getLanguages() != null ? new ArrayList<>(actor.getLanguages()) : new ArrayList<>();

        return ActorDetailResponse.builder()
                .id(actor.getUserId())
                .email(actor.getUser().getEmail())
                .name(actor.getUser().getName())
                .stageName(actor.getStageName())
                .profileImage(actor.getUser().getProfileImage())
                .birthYear(actor.getBirthYear())
                .introduction(actor.getIntroduction())
                .nationality(actor.getNationality())
                .height(actor.getHeight())
                .weight(actor.getWeight())
                .skills(skillsCopy)
                .languages(languagesCopy)
                .agency(actor.getAgency())
                .isProfileComplete(actor.getIsProfileComplete())
                .viewCount(viewCount)
                .likeCount(likeCount)
                .createdAt(actor.getCreatedAt())
                .updatedAt(actor.getUpdatedAt())
                .build();
    }
}

