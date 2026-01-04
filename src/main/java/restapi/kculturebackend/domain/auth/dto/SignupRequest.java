package restapi.kculturebackend.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.user.entity.UserType;

/**
 * 회원가입 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    @NotNull(message = "사용자 유형은 필수입니다.")
    private UserType type;

    @NotNull(message = "이용약관 동의는 필수입니다.")
    private Boolean termsAgreed;

    @NotNull(message = "개인정보처리방침 동의는 필수입니다.")
    private Boolean privacyAgreed;

    private Boolean marketingAgreed;
}

