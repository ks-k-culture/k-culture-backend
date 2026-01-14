package restapi.kculturebackend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 변경 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 변경 요청")
public class ChangePasswordRequest {

    @Schema(description = "현재 비밀번호", example = "currentPassword123!")
    @NotBlank(message = "현재 비밀번호는 필수입니다")
    private String currentPassword;

    @Schema(description = "새 비밀번호 (8자 이상, 영문/숫자/특수문자 포함)", example = "newPassword123!")
    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", example = "newPassword123!")
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;
}
