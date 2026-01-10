package restapi.kculturebackend.domain.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.project.dto.CharacterResponse;
import restapi.kculturebackend.domain.project.dto.CreateCharacterRequest;
import restapi.kculturebackend.domain.project.dto.UpdateCharacterRequest;
import restapi.kculturebackend.domain.project.entity.Character;
import restapi.kculturebackend.domain.project.entity.Project;
import restapi.kculturebackend.domain.project.repository.CharacterRepository;
import restapi.kculturebackend.domain.project.repository.ProjectRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 캐릭터 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final ProjectRepository projectRepository;

    /**
     * 프로젝트의 캐릭터 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CharacterResponse> getCharactersByProject(UUID projectId) {
        // 프로젝트 존재 확인
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException(ErrorCode.PROJECT_NOT_FOUND);
        }

        return characterRepository.findByProjectId(projectId).stream()
                .map(CharacterResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 캐스팅 미완료 캐릭터 목록
     */
    @Transactional(readOnly = true)
    public List<CharacterResponse> getOpenCastings(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException(ErrorCode.PROJECT_NOT_FOUND);
        }

        return characterRepository.findOpenCastings(projectId).stream()
                .map(CharacterResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 캐릭터 상세 조회
     */
    @Transactional(readOnly = true)
    public CharacterResponse getCharacter(UUID characterId) {
        Character character = characterRepository.findByIdWithProject(characterId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHARACTER_NOT_FOUND));

        return CharacterResponse.from(character);
    }

    /**
     * 캐릭터 생성
     */
    @Transactional
    public CharacterResponse createCharacter(User user, UUID projectId, CreateCharacterRequest request) {
        validateAgencyUser(user);

        Project project = projectRepository.findByIdWithAgency(projectId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        // 본인의 프로젝트인지 확인
        if (!project.getAgency().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 프로젝트에만 캐릭터를 추가할 수 있습니다.");
        }

        Character character = Character.create(
                project,
                request.getName(),
                request.getGender(),
                request.getAgeRange(),
                request.getRoleType(),
                request.getDescription(),
                request.getKeywords(),
                request.getFee()
        );

        Character saved = characterRepository.save(character);
        log.info("Character created: {} for project: {}", saved.getId(), projectId);

        return CharacterResponse.from(saved);
    }

    /**
     * 캐릭터 수정
     */
    @Transactional
    public CharacterResponse updateCharacter(User user, UUID characterId, UpdateCharacterRequest request) {
        validateAgencyUser(user);

        Character character = characterRepository.findByIdWithProject(characterId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHARACTER_NOT_FOUND));

        // 본인의 프로젝트인지 확인
        if (!character.getProject().getAgency().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 프로젝트 캐릭터만 수정할 수 있습니다.");
        }

        character.update(
                request.getName(),
                request.getGender(),
                request.getAgeRange(),
                request.getRoleType(),
                request.getDescription(),
                request.getKeywords(),
                request.getFee()
        );

        Character saved = characterRepository.save(character);
        log.info("Character updated: {}", characterId);

        return CharacterResponse.from(saved);
    }

    /**
     * 캐릭터 삭제
     */
    @Transactional
    public void deleteCharacter(User user, UUID characterId) {
        validateAgencyUser(user);

        Character character = characterRepository.findByIdWithProject(characterId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHARACTER_NOT_FOUND));

        // 본인의 프로젝트인지 확인
        if (!character.getProject().getAgency().getUserId().equals(user.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 프로젝트 캐릭터만 삭제할 수 있습니다.");
        }

        characterRepository.delete(character);
        log.info("Character deleted: {}", characterId);
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

