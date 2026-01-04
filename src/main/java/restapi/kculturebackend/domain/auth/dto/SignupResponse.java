package restapi.kculturebackend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.UUID;

/**
 * 회원가입 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {
    private UUID userId;
    private String email;
    private UserType type;
}

