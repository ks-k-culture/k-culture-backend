package restapi.kculturebackend.domain.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.common.dto.PaginationResponse;
import restapi.kculturebackend.domain.notice.dto.NoticeDetailResponse;
import restapi.kculturebackend.domain.notice.dto.NoticeSummaryResponse;
import restapi.kculturebackend.domain.notice.entity.NoticeType;
import restapi.kculturebackend.domain.notice.service.NoticeService;

import java.util.UUID;

/**
 * 공지사항 API 컨트롤러
 */
@Tag(name = "Notices", description = "공지사항 관련 API (목록, 상세)")
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<NoticeSummaryResponse>>> getNotices(
            @Parameter(description = "공지사항 유형") @RequestParam(required = false) NoticeType type,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<NoticeSummaryResponse> notices = noticeService.getNotices(type, pageable);
        return ResponseEntity.ok(ApiResponse.success(PaginationResponse.from(notices)));
    }

    // 공지사항 상세 조회
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상세 내용을 조회합니다.")
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(
            @Parameter(description = "공지사항 ID") @PathVariable UUID noticeId) {

        NoticeDetailResponse notice = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.success(notice));
    }
}
