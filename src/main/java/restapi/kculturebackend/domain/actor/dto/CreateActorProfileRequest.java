package restapi.kculturebackend.domain.actor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배우 프로필 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateActorProfileRequest {

    @NotBlank(message = "활동명은 필수입니다.")
    @Size(max = 50, message = "활동명은 50자 이내여야 합니다.")
    private String name;

    @NotBlank(message = "한 줄 소개는 필수입니다.")
    @Size(max = 500, message = "한 줄 소개는 500자 이내여야 합니다.")
    private String introduction;

    @NotBlank(message = "나이대는 필수입니다.")
    private String ageGroup;
}
