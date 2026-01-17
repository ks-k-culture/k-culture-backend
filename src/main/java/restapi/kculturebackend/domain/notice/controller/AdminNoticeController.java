package restapi.kculturebackend.domain.notice.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.domain.notice.dto.CreateNoticeRequest;
import restapi.kculturebackend.domain.notice.dto.NoticeDetailResponse;
import restapi.kculturebackend.domain.notice.dto.UpdateNoticeRequest;
import restapi.kculturebackend.domain.notice.service.NoticeService;
import restapi.kculturebackend.domain.user.entity.User;

/**
 * 관리자용 공지사항 API 컨트롤러
 */
@Tag(name = "Admin - Notices", description = "관리자 전용 공지사항 관리 API")
@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 등록", description = "새 공지사항을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createNotice(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateNoticeRequest request) {

        NoticeDetailResponse notice = noticeService.createNotice(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "id", notice.getId(),
                "message", "공지사항이 등록되었습니다."
        )));
    }

    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다.")
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateNotice(
            @AuthenticationPrincipal User user,
            @Parameter(description = "공지사항 ID") @PathVariable UUID noticeId,
            @Valid @RequestBody UpdateNoticeRequest request) {

        NoticeDetailResponse notice = noticeService.updateNotice(noticeId, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "id", notice.getId(),
                "message", "공지사항이 수정되었습니다."
        )));
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteNotice(
            @AuthenticationPrincipal User user,
            @Parameter(description = "공지사항 ID") @PathVariable UUID noticeId) {

        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "공지사항이 삭제되었습니다."
        )));
    }
}
