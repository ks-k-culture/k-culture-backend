package restapi.kculturebackend.domain.actor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 배우 프로필 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdateActorProfileRequest {

    @Size(max = 50, message = "활동명은 50자 이내여야 합니다.")
    private String stageName;

    @Min(value = 1900, message = "출생년도가 올바르지 않습니다.")
    @Max(value = 2100, message = "출생년도가 올바르지 않습니다.")
    private Integer birthYear;

    @Size(max = 500, message = "소개는 500자 이내여야 합니다.")
    private String introduction;

    @Size(max = 50, message = "국적은 50자 이내여야 합니다.")
    private String nationality;

    @Min(value = 100, message = "키는 100cm 이상이어야 합니다.")
    @Max(value = 250, message = "키는 250cm 이하여야 합니다.")
    private Integer height;

    @Min(value = 30, message = "몸무게는 30kg 이상이어야 합니다.")
    @Max(value = 200, message = "몸무게는 200kg 이하여야 합니다.")
    private Integer weight;

    private List<String> skills;

    private List<String> languages;

    @Size(max = 100, message = "소속사명은 100자 이내여야 합니다.")
    private String agency;
}

