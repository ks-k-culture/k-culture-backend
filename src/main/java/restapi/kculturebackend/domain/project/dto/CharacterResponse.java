package restapi.kculturebackend.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.actor.entity.RoleType;
import restapi.kculturebackend.domain.project.entity.Character;
import restapi.kculturebackend.domain.project.entity.Gender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 캐릭터 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterResponse {
    private UUID id;
    private UUID projectId;
    private String projectName;
    private String name;
    private Gender gender;
    private String genderDisplayName;
    private String ageRange;
    private RoleType roleType;
    private String roleTypeDisplayName;
    private String description;
    private List<String> keywords;
    private String fee;
    private Boolean isCastingComplete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CharacterResponse from(Character character) {
        return CharacterResponse.builder()
                .id(character.getId())
                .projectId(character.getProject().getId())
                .projectName(character.getProject().getProjectName())
                .name(character.getName())
                .gender(character.getGender())
                .genderDisplayName(character.getGender() != null ? character.getGender().getDisplayName() : null)
                .ageRange(character.getAgeRange())
                .roleType(character.getRoleType())
                .roleTypeDisplayName(character.getRoleType() != null ? character.getRoleType().getDisplayName() : null)
                .description(character.getDescription())
                .keywords(character.getKeywords())
                .fee(character.getFee())
                .isCastingComplete(character.getIsCastingComplete())
                .createdAt(character.getCreatedAt())
                .updatedAt(character.getUpdatedAt())
                .build();
    }
}

