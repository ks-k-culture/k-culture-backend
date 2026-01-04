package restapi.kculturebackend.domain.agency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.agency.entity.AgencyProfile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 에이전시 프로필 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyProfileResponse {
    private UUID userId;
    private String email;
    private String name;
    private String profileImage;
    private String agencyName;
    private String representativeName;
    private String foundedYear;
    private String businessNumber;
    private String address;
    private String phone;
    private String website;
    private String introduction;
    private List<String> specialties;
    private Boolean isProfileComplete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AgencyProfileResponse from(AgencyProfile agency) {
        return AgencyProfileResponse.builder()
                .userId(agency.getUserId())
                .email(agency.getUser().getEmail())
                .name(agency.getUser().getName())
                .profileImage(agency.getUser().getProfileImage())
                .agencyName(agency.getAgencyName())
                .representativeName(agency.getRepresentativeName())
                .foundedYear(agency.getFoundedYear())
                .businessNumber(agency.getBusinessNumber())
                .address(agency.getAddress())
                .phone(agency.getPhone())
                .website(agency.getWebsite())
                .introduction(agency.getIntroduction())
                .specialties(agency.getSpecialties())
                .isProfileComplete(agency.getIsProfileComplete())
                .createdAt(agency.getCreatedAt())
                .updatedAt(agency.getUpdatedAt())
                .build();
    }
}

