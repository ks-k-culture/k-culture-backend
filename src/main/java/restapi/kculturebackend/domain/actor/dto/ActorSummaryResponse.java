package restapi.kculturebackend.domain.actor.dto;

import java.time.Year;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;

/**
 * 배우 목록 조회용 요약 응답 DTO
 */
@Getter
@Builder
public class ActorSummaryResponse {
    private UUID id;
    private String email;
    private String name;
    private String stageName;
    private String profileImage;
    private Integer birthYear;
    private Integer age;
    private String gender;
    private String category;
    private Integer height;
    private Integer weight;
    private String introduction;
    private String agency;
    private List<String> skills;
    private Long viewCount;

    public static ActorSummaryResponse from(ActorProfile actor) {
        Integer age = actor.getBirthYear() != null 
                ? Year.now().getValue() - actor.getBirthYear() 
                : null;
        
        return ActorSummaryResponse.builder()
                .id(actor.getUserId())
                .email(actor.getUser().getEmail())
                .name(actor.getUser().getName())
                .stageName(actor.getStageName())
                .profileImage(actor.getUser().getProfileImage())
                .birthYear(actor.getBirthYear())
                .age(age)
                .gender(actor.getGender() != null ? actor.getGender().getDisplayName() : null)
                .category(actor.getCategory() != null ? actor.getCategory().getDisplayName() : null)
                .height(actor.getHeight())
                .weight(actor.getWeight())
                .introduction(actor.getIntroduction())
                .agency(actor.getAgency())
                .skills(actor.getSkills())
                .viewCount(actor.getViewCount())
                .build();
    }
}

