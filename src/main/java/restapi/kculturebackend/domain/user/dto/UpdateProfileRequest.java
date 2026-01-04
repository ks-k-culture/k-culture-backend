package restapi.kculturebackend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 프로필 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String name;
    private String position;
    private String agency;
    private String phone;
    private String bio;
    private String fee;
    private Integer height;
    private Integer weight;
}

