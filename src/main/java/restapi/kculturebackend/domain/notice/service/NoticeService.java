package restapi.kculturebackend.domain.notice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.notice.dto.NoticeDetailResponse;
import restapi.kculturebackend.domain.notice.dto.NoticeSummaryResponse;
import restapi.kculturebackend.domain.notice.entity.Notice;
import restapi.kculturebackend.domain.notice.entity.NoticeRead;
import restapi.kculturebackend.domain.notice.entity.NoticeType;
import restapi.kculturebackend.domain.notice.repository.NoticeReadRepository;
import restapi.kculturebackend.domain.notice.repository.NoticeRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.repository.UserRepository;

/**
 * 공지사항 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeReadRepository noticeReadRepository;
    private final UserRepository userRepository;

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public Page<NoticeSummaryResponse> getNotices(NoticeType type, Pageable pageable) {
        Page<Notice> notices;

        if (type != null) {
            notices = noticeRepository.findByType(type, pageable);
        } else {
            notices = noticeRepository.findAll(pageable);
        }

        return notices.map(NoticeSummaryResponse::from);
    }

    // 공지사항 상세 조회
    @Transactional
    public NoticeDetailResponse getNotice(UUID noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));

        // 조회수 증가
        notice.incrementViews();
        noticeRepository.save(notice);

        return NoticeDetailResponse.from(notice);
    }

    // 공지사항 읽음 표시
    @Transactional
    public void markAsRead(UUID noticeId, UUID userId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 이미 읽음 표시된 경우 무시
        if (noticeReadRepository.existsByUserAndNotice(user, notice)) {
            return;
        }

        NoticeRead noticeRead = NoticeRead.create(user, notice);
        noticeReadRepository.save(noticeRead);
    }

    // 사용자가 읽은 공지사항 ID 목록 조회
    @Transactional(readOnly = true)
    public List<UUID> getReadNoticeIds(UUID userId) {
        return noticeReadRepository.findReadNoticeIdsByUserId(userId);
    }

    // 특정 공지사항 읽음 여부 확인
    @Transactional(readOnly = true)
    public boolean isRead(UUID noticeId, UUID userId) {
        Notice notice = noticeRepository.findById(noticeId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (notice == null || user == null) {
            return false;
        }

        return noticeReadRepository.existsByUserAndNotice(user, notice);
    }
}
