package restapi.kculturebackend.domain.actor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 쇼릴 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShowreelRequest {

    @NotBlank(message = "쇼릴 제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이내여야 합니다.")
    private String title;

    @NotBlank(message = "영상 URL은 필수입니다.")
    @Size(max = 500, message = "영상 URL은 500자 이내여야 합니다.")
    private String videoUrl;

    @Size(max = 500, message = "썸네일 URL은 500자 이내여야 합니다.")
    private String thumbnailUrl;

    private Integer duration; // 영상 길이 (초)

    @Size(max = 200, message = "작품명은 200자 이내여야 합니다.")
    private String workTitle;

    @Size(max = 50, message = "장르는 50자 이내여야 합니다.")
    private String genre;

    @Size(max = 1000, message = "설명은 1000자 이내여야 합니다.")
    private String description;

    private List<String> tags;
}

