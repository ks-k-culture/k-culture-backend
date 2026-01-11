package restapi.kculturebackend.domain.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restapi.kculturebackend.domain.job.entity.Job;
import restapi.kculturebackend.domain.job.entity.JobCategory;
import restapi.kculturebackend.domain.job.entity.JobStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * 작품구인 레포지토리
 */
public interface JobRepository extends JpaRepository<Job, UUID> {

    /**
     * 작품구인 목록 조회 (페이징)
     */
    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    /**
     * 작품구인 목록 조회 (카테고리별)
     */
    Page<Job> findByCategory(JobCategory category, Pageable pageable);

    /**
     * 작품구인 목록 조회 (상태 + 카테고리)
     */
    Page<Job> findByStatusAndCategory(JobStatus status, JobCategory category, Pageable pageable);

    /**
     * 검색
     */
    @Query("SELECT j FROM Job j WHERE " +
           "(:status IS NULL OR j.status = :status) AND " +
           "(:category IS NULL OR j.category = :category) AND " +
           "(:gender IS NULL OR j.gender = :gender) AND " +
           "(:isPumasi IS NULL OR j.isPumasi = :isPumasi) AND " +
           "(:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(j.workTitle) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> searchJobs(
            @Param("status") JobStatus status,
            @Param("category") JobCategory category,
            @Param("gender") String gender,
            @Param("isPumasi") Boolean isPumasi,
            @Param("search") String search,
            Pageable pageable);

    /**
     * 사용자의 작품구인 조회
     */
    @Query("SELECT j FROM Job j WHERE j.id = :jobId AND j.user.id = :userId")
    Optional<Job> findByIdAndUserId(@Param("jobId") UUID jobId, @Param("userId") UUID userId);
}
