package restapi.kculturebackend.domain.actor.dto;

import lombok.Builder;
import lombok.Getter;
import restapi.kculturebackend.domain.actor.entity.ActorProfile;

import java.util.UUID;

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
    private String introduction;
    private String agency;

    public static ActorSummaryResponse from(ActorProfile actor) {
        return ActorSummaryResponse.builder()
                .id(actor.getUserId())
                .email(actor.getUser().getEmail())
                .name(actor.getUser().getName())
                .stageName(actor.getStageName())
                .profileImage(actor.getUser().getProfileImage())
                .birthYear(actor.getBirthYear())
                .introduction(actor.getIntroduction())
                .agency(actor.getAgency())
                .build();
    }
}

