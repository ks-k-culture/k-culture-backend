package restapi.kculturebackend.common.dto;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 페이지네이션 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
    private int page;
    private int limit;
    private long total;
    private int totalPages;

    public static <T> Pagination from(Page<T> page) {
        return new Pagination(
                page.getNumber() + 1, // 0-based to 1-based
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
