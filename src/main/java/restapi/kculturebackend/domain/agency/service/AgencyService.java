package restapi.kculturebackend.domain.agency.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.agency.dto.AgencyProfileResponse;
import restapi.kculturebackend.domain.agency.dto.UpdateAgencyProfileRequest;
import restapi.kculturebackend.domain.agency.entity.AgencyProfile;
import restapi.kculturebackend.domain.agency.repository.AgencyProfileRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.UUID;

/**
 * 에이전시 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyProfileRepository agencyProfileRepository;

    /**
     * 에이전시 프로필 조회 (공개)
     */
    @Transactional(readOnly = true)
    public AgencyProfileResponse getAgencyProfile(UUID agencyId) {
        AgencyProfile agency = agencyProfileRepository.findByUserIdWithUser(agencyId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.AGENCY_PROFILE_NOT_FOUND));

        return AgencyProfileResponse.from(agency);
    }

    /**
     * 내 에이전시 프로필 조회
     */
    @Transactional(readOnly = true)
    public AgencyProfileResponse getMyProfile(User user) {
        validateAgencyUser(user);

        AgencyProfile agency = agencyProfileRepository.findByUserIdWithUser(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.AGENCY_PROFILE_NOT_FOUND));

        return AgencyProfileResponse.from(agency);
    }

    /**
     * 에이전시 프로필 수정
     */
    @Transactional
    public AgencyProfileResponse updateProfile(User user, UpdateAgencyProfileRequest request) {
        validateAgencyUser(user);

        AgencyProfile agency = agencyProfileRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.AGENCY_PROFILE_NOT_FOUND));

        agency.updateProfile(
                request.getAgencyName(),
                request.getRepresentativeName(),
                request.getFoundedYear(),
                request.getBusinessNumber(),
                request.getAddress(),
                request.getPhone(),
                request.getWebsite(),
                request.getIntroduction(),
                request.getSpecialties()
        );

        AgencyProfile savedAgency = agencyProfileRepository.save(agency);
        log.info("Agency profile updated: {}", user.getId());

        return AgencyProfileResponse.from(savedAgency);
    }

    /**
     * 사용자가 에이전시 타입인지 검증
     */
    private void validateAgencyUser(User user) {
        if (user.getType() != UserType.AGENCY) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "에이전시 계정만 접근할 수 있습니다.");
        }
    }
}

