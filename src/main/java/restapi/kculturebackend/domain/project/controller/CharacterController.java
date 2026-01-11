package restapi.kculturebackend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.domain.project.dto.CharacterResponse;
import restapi.kculturebackend.domain.project.dto.CreateCharacterRequest;
import restapi.kculturebackend.domain.project.dto.UpdateCharacterRequest;
import restapi.kculturebackend.domain.project.service.CharacterService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 캐릭터(배역) API 컨트롤러
 */
@Tag(name = "Characters", description = "캐릭터 관련 API (CRUD)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    /**
     * 프로젝트별 캐릭터 목록 조회
     */
    @Operation(summary = "캐릭터 목록 조회", description = "프로젝트에 속한 캐릭터 목록을 조회합니다.")
    @GetMapping("/projects/{projectId}/characters")
    public ResponseEntity<ApiResponse<Map<String, List<CharacterResponse>>>> getProjectCharacters(
            @Parameter(description = "프로젝트 ID") @PathVariable UUID projectId) {

        List<CharacterResponse> characters = characterService.getCharactersByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("characters", characters)));
    }

    /**
     * 캐릭터 생성
     */
    @Operation(summary = "캐릭터 생성", description = "프로젝트에 새로운 캐릭터를 추가합니다.")
    @PostMapping("/projects/{projectId}/characters")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCharacter(
            @AuthenticationPrincipal User user,
            @Parameter(description = "프로젝트 ID") @PathVariable UUID projectId,
            @Valid @RequestBody CreateCharacterRequest request) {

        CharacterResponse character = characterService.createCharacter(user, projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of(
                        "id", character.getId(),
                        "name", character.getName()
                )));
    }

    /**
     * 캐릭터 수정
     */
    @Operation(summary = "캐릭터 수정", description = "캐릭터 정보를 수정합니다.")
    @PutMapping("/characters/{characterId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCharacter(
            @AuthenticationPrincipal User user,
            @Parameter(description = "캐릭터 ID") @PathVariable UUID characterId,
            @Valid @RequestBody UpdateCharacterRequest request) {

        CharacterResponse character = characterService.updateCharacter(user, characterId, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "id", character.getId(),
                "name", character.getName()
        )));
    }

    /**
     * 캐릭터 삭제
     */
    @Operation(summary = "캐릭터 삭제", description = "캐릭터를 삭제합니다.")
    @DeleteMapping("/characters/{characterId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteCharacter(
            @AuthenticationPrincipal User user,
            @Parameter(description = "캐릭터 ID") @PathVariable UUID characterId) {

        characterService.deleteCharacter(user, characterId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "캐릭터가 삭제되었습니다.")));
    }
}
