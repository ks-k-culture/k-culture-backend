package restapi.kculturebackend.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.actor.entity.RoleType;
import restapi.kculturebackend.domain.project.entity.Gender;

import java.util.List;

/**
 * 캐릭터 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCharacterRequest {

    @NotBlank(message = "배역명은 필수입니다.")
    @Size(max = 100, message = "배역명은 100자 이내여야 합니다.")
    private String name;

    private Gender gender;

    @Size(max = 30, message = "나이대는 30자 이내여야 합니다.")
    private String ageRange;

    private RoleType roleType;

    @Size(max = 1000, message = "캐릭터 설명은 1000자 이내여야 합니다.")
    private String description;

    private List<String> keywords;

    @Size(max = 50, message = "출연료 정보는 50자 이내여야 합니다.")
    private String fee;
}

