package restapi.kculturebackend.domain.agency.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 에이전시 프로필 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAgencyProfileRequest {

    @Size(max = 100, message = "에이전시명은 100자 이내여야 합니다.")
    private String agencyName;

    @Size(max = 50, message = "대표자명은 50자 이내여야 합니다.")
    private String representativeName;

    @Size(max = 4, message = "설립년도는 4자리여야 합니다.")
    private String foundedYear;

    @Size(max = 20, message = "사업자등록번호는 20자 이내여야 합니다.")
    private String businessNumber;

    @Size(max = 200, message = "주소는 200자 이내여야 합니다.")
    private String address;

    @Size(max = 20, message = "연락처는 20자 이내여야 합니다.")
    private String phone;

    @Size(max = 200, message = "웹사이트 URL은 200자 이내여야 합니다.")
    private String website;

    @Size(max = 1000, message = "회사 소개는 1000자 이내여야 합니다.")
    private String introduction;

    private List<String> specialties;
}

