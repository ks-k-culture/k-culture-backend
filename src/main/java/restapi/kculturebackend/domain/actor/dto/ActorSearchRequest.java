package restapi.kculturebackend.domain.actor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배우 검색 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActorSearchRequest {
    
    // 구분 (배우/모델)
    private String category;
    
    // 성별
    private String gender;
    
    // 나이 범위
    private Integer ageMin;
    private Integer ageMax;
    
    // 키 범위 (cm)
    private Integer heightMin;
    private Integer heightMax;
    
    // 몸무게 범위 (kg)
    private Integer weightMin;
    private Integer weightMax;
    
    // 특기/스킬 목록
    private List<String> skills;
    
    // 키워드 검색 (이름, 활동명)
    private String keyword;
    
    // 정렬 기준
    private String sortBy;
    
    // 정렬 방향
    private String sortDirection;
}
