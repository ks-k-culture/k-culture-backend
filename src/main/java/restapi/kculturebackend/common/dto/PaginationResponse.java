package restapi.kculturebackend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * 페이지네이션 응답 구조
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse {
    private int page;
    private int limit;
    private long total;
    private int totalPages;

    /**
     * Spring Data Page 객체로부터 PaginationResponse 생성
     */
    public static PaginationResponse from(Page<?> page) {
        return new PaginationResponse(
                page.getNumber() + 1, // 0-based to 1-based
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

