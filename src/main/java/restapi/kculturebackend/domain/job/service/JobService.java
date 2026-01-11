package restapi.kculturebackend.domain.job.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.kculturebackend.common.exception.ErrorCode;
import restapi.kculturebackend.common.exception.ForbiddenException;
import restapi.kculturebackend.common.exception.NotFoundException;
import restapi.kculturebackend.domain.job.dto.*;
import restapi.kculturebackend.domain.job.entity.Job;
import restapi.kculturebackend.domain.job.entity.JobCategory;
import restapi.kculturebackend.domain.job.entity.JobStatus;
import restapi.kculturebackend.domain.job.repository.JobRepository;
import restapi.kculturebackend.domain.user.entity.User;

import java.util.UUID;

/**
 * 작품구인 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    // 작품구인 목록 조회 (검색/필터)
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getJobs(JobCategory category, String gender, String ageRange,
                                             Boolean isPumasi, String status, String search, Pageable pageable) {
        JobStatus jobStatus = null;
        if (status != null && !"all".equalsIgnoreCase(status)) {
            jobStatus = "모집중".equals(status) ? JobStatus.RECRUITING : JobStatus.CLOSED;
        }

        Page<Job> jobs = jobRepository.searchJobs(jobStatus, category, gender, isPumasi, search, pageable);
        return jobs.map(JobSummaryResponse::from);
    }

    // 작품구인 상세 조회
    @Transactional
    public JobDetailResponse getJob(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.JOB_NOT_FOUND));

        // 조회수 증가
        job.incrementViews();
        jobRepository.save(job);

        return JobDetailResponse.from(job);
    }

    // 작품구인 생성
    @Transactional
    public JobDetailResponse createJob(User user, CreateJobRequest request) {
        Job job = Job.builder()
                .user(user)
                .category(request.getCategory())
                .isPumasi(request.getIsPumasi() != null ? request.getIsPumasi() : false)
                .price(request.getPrice())
                .title(request.getTitle())
                .description(request.getDescription())
                .gender(request.getGender())
                .ageRange(request.getAgeRange())
                .production(request.getProduction())
                .workTitle(request.getWorkTitle())
                .shootingDate(request.getShootingDate())
                .shootingLocation(request.getShootingLocation())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .build();

        Job saved = jobRepository.save(job);
        log.info("Job created: id={}, title={}", saved.getId(), saved.getTitle());

        return JobDetailResponse.from(saved);
    }

    // 작품구인 수정
    @Transactional
    public JobDetailResponse updateJob(User user, UUID jobId, UpdateJobRequest request) {
        Job job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(() -> {
                    if (!jobRepository.existsById(jobId)) {
                        return new NotFoundException(ErrorCode.JOB_NOT_FOUND);
                    }
                    return new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 작품구인만 수정할 수 있습니다.");
                });

        job.update(
                request.getCategory(),
                request.getIsPumasi(),
                request.getPrice(),
                request.getTitle(),
                request.getDescription(),
                request.getGender(),
                request.getAgeRange(),
                request.getProduction(),
                request.getWorkTitle(),
                request.getShootingDate(),
                request.getShootingLocation(),
                request.getContactEmail(),
                request.getContactPhone()
        );

        Job saved = jobRepository.save(job);
        log.info("Job updated: id={}", jobId);

        return JobDetailResponse.from(saved);
    }

    // 작품구인 삭제
    @Transactional
    public void deleteJob(User user, UUID jobId) {
        Job job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(() -> {
                    if (!jobRepository.existsById(jobId)) {
                        return new NotFoundException(ErrorCode.JOB_NOT_FOUND);
                    }
                    return new ForbiddenException(ErrorCode.FORBIDDEN, "본인의 작품구인만 삭제할 수 있습니다.");
                });

        jobRepository.delete(job);
        log.info("Job deleted: id={}", jobId);
    }
}
