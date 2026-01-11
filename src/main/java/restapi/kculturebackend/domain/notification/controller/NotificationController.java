package restapi.kculturebackend.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.common.dto.PaginationResponse;
import restapi.kculturebackend.domain.notification.dto.NotificationResponse;
import restapi.kculturebackend.domain.notification.entity.NotificationType;
import restapi.kculturebackend.domain.notification.service.NotificationService;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.Map;
import java.util.UUID;

/**
 * 알림 API 컨트롤러
 */
@Tag(name = "Notifications", description = "알림 관련 API (목록, 읽음 처리)")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 목록 조회
     */
    @Operation(summary = "알림 목록 조회", description = "로그인한 사용자의 알림 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications(
            @AuthenticationPrincipal User user,
            @Parameter(description = "읽음 상태") @RequestParam(required = false) Boolean isRead,
            @Parameter(description = "알림 타입") @RequestParam(required = false) NotificationType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<NotificationResponse> notifications = notificationService.getNotifications(user, isRead, type, pageable);
        long unreadCount = notificationService.getUnreadCount(user);

        PaginationResponse<NotificationResponse> paginationResponse = PaginationResponse.from(notifications);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "notifications", paginationResponse.getContent(),
                "unreadCount", unreadCount,
                "pagination", Map.of(
                        "page", paginationResponse.getPage(),
                        "limit", paginationResponse.getLimit(),
                        "total", paginationResponse.getTotal(),
                        "totalPages", paginationResponse.getTotalPages()
                )
        )));
    }

    /**
     * 알림 읽음 처리
     */
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal User user,
            @Parameter(description = "알림 ID") @PathVariable UUID notificationId) {

        notificationService.markAsRead(user, notificationId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 모든 알림 읽음 처리
     */
    @Operation(summary = "모든 알림 읽음 처리", description = "모든 알림을 읽음 상태로 변경합니다.")
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead(
            @AuthenticationPrincipal User user) {

        int updatedCount = notificationService.markAllAsRead(user);
        return ResponseEntity.ok(ApiResponse.success(Map.of("updatedCount", updatedCount)));
    }
}
