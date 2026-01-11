package restapi.kculturebackend.domain.agency.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 에이전시 프로필 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAgencyProfileRequest {

    @NotBlank(message = "에이전시명은 필수입니다.")
    @Size(max = 100, message = "에이전시명은 100자 이내여야 합니다.")
    private String agencyName;

    @NotBlank(message = "대표자명은 필수입니다.")
    @Size(max = 50, message = "대표자명은 50자 이내여야 합니다.")
    private String representativeName;

    @NotBlank(message = "설립년도는 필수입니다.")
    @Pattern(regexp = "^\\d{4}$", message = "설립년도는 4자리 숫자여야 합니다.")
    private String foundedYear;

    @NotEmpty(message = "전문 분야는 최소 1개 이상 필요합니다.")
    private List<String> specialties;
}
