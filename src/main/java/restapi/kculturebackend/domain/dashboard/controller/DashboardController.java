package restapi.kculturebackend.domain.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restapi.kculturebackend.common.dto.ApiResponse;
import restapi.kculturebackend.domain.dashboard.dto.ActorDashboardStats;
import restapi.kculturebackend.domain.dashboard.dto.AgencyDashboardStats;
import restapi.kculturebackend.domain.dashboard.service.DashboardService;
import restapi.kculturebackend.domain.user.entity.User;

/**
 * 대시보드 API 컨트롤러
 */
@Tag(name = "Dashboard", description = "대시보드 관련 API (통계)")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // 대시보드 통계 조회
    @Operation(summary = "대시보드 통계 조회", description = "사용자 유형에 따른 대시보드 통계 정보를 조회합니다.")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<?>> getDashboardStats(@AuthenticationPrincipal User user) {
        if (dashboardService.isActor(user)) {
            ActorDashboardStats stats = dashboardService.getActorStats(user);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } else {
            AgencyDashboardStats stats = dashboardService.getAgencyStats(user);
            return ResponseEntity.ok(ApiResponse.success(stats));
        }
    }
}
