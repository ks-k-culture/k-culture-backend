package restapi.kculturebackend.domain.actor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 배우 연락하기 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactActorRequest {

    @NotBlank(message = "메시지는 필수입니다.")
    private String message;

    private UUID projectId;
    private UUID characterId;
}
