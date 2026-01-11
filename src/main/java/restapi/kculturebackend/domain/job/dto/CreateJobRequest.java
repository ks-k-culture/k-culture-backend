package restapi.kculturebackend.domain.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.job.entity.JobCategory;

/**
 * 작품구인 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private JobCategory category;

    @Builder.Default
    private Boolean isPumasi = false;

    private Integer price;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이내여야 합니다.")
    private String title;

    @Size(max = 2000, message = "설명은 2000자 이내여야 합니다.")
    private String description;

    @NotBlank(message = "성별은 필수입니다.")
    private String gender;

    private String ageRange;

    @NotBlank(message = "제작사는 필수입니다.")
    private String production;

    @NotBlank(message = "작품 제목은 필수입니다.")
    private String workTitle;

    private String shootingDate;

    private String shootingLocation;

    private String contactEmail;

    private String contactPhone;
}
