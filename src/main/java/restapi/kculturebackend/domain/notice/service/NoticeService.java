package restapi.kculturebackend.domain.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.notice.dto.NoticeDetailResponse;
import restapi.kculturebackend.domain.notice.dto.NoticeSummaryResponse;
import restapi.kculturebackend.domain.notice.entity.Notice;
import restapi.kculturebackend.domain.notice.entity.NoticeType;
import restapi.kculturebackend.domain.notice.repository.NoticeRepository;

import java.util.UUID;

/**
 * 공지사항 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 목록 조회
     */
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

    /**
     * 공지사항 상세 조회
     */
    @Transactional
    public NoticeDetailResponse getNotice(UUID noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));

        // 조회수 증가
        notice.incrementViews();
        noticeRepository.save(notice);

        return NoticeDetailResponse.from(notice);
    }
}
