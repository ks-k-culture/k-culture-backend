package restapi.kculturebackend.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import restapi.kculturebackend.domain.job.entity.Job;
import restapi.kculturebackend.domain.job.entity.JobCategory;
import restapi.kculturebackend.domain.job.entity.JobStatus;

/**
 * Job 엔티티 단위 테스트
 */
@DisplayName("Job 엔티티 테스트")
class JobEntityTest {

    @Test
    @DisplayName("Job 생성 시 기본값이 올바르게 설정된다")
    void createJob_defaultValues() {
        // given & when
        Job job = Job.builder()
                .category(JobCategory.SHORT_FILM)
                .title("테스트 작품구인")
                .production("테스트 제작사")
                .workTitle("테스트 작품")
                .build();

        // then
        assertThat(job.getStatus()).isEqualTo(JobStatus.RECRUITING);
        assertThat(job.getViews()).isEqualTo(0);
        assertThat(job.getIsPumasi()).isFalse();
    }

    @Test
    @DisplayName("조회수 증가 메서드가 정상 동작한다")
    void incrementViews_success() {
        // given
        Job job = Job.builder()
                .category(JobCategory.ADVERTISEMENT)
                .title("광고 작품")
                .production("광고사")
                .workTitle("광고")
                .build();

        // when
        job.incrementViews();
        job.incrementViews();
        job.incrementViews();

        // then
        assertThat(job.getViews()).isEqualTo(3);
    }

    @Test
    @DisplayName("마감 처리 메서드가 정상 동작한다")
    void close_success() {
        // given
        Job job = Job.builder()
                .category(JobCategory.WEB_DRAMA)
                .title("웹드라마")
                .production("제작사")
                .workTitle("작품")
                .build();

        assertThat(job.getStatus()).isEqualTo(JobStatus.RECRUITING);

        // when
        job.close();

        // then
        assertThat(job.getStatus()).isEqualTo(JobStatus.CLOSED);
    }

    @Test
    @DisplayName("Job 수정 메서드가 정상 동작한다")
    void update_success() {
        // given
        Job job = Job.builder()
                .category(JobCategory.SHORT_FILM)
                .title("원래 제목")
                .production("원래 제작사")
                .workTitle("원래 작품")
                .build();

        // when
        job.update(
                JobCategory.FEATURE_FILM,
                true,
                1000000,
                "수정된 제목",
                "수정된 설명",
                "여성",
                "20대",
                "수정된 제작사",
                "수정된 작품",
                "2026-02-01",
                "서울",
                "test@test.com",
                "010-1234-5678"
        );

        // then
        assertThat(job.getCategory()).isEqualTo(JobCategory.FEATURE_FILM);
        assertThat(job.getIsPumasi()).isTrue();
        assertThat(job.getPrice()).isEqualTo(1000000);
        assertThat(job.getTitle()).isEqualTo("수정된 제목");
        assertThat(job.getDescription()).isEqualTo("수정된 설명");
        assertThat(job.getGender()).isEqualTo("여성");
        assertThat(job.getProduction()).isEqualTo("수정된 제작사");
    }

    @Test
    @DisplayName("JobCategory enum이 올바른 displayName을 반환한다")
    void jobCategory_displayName() {
        assertThat(JobCategory.SHORT_FILM.getDisplayName()).isEqualTo("단편영화");
        assertThat(JobCategory.FEATURE_FILM.getDisplayName()).isEqualTo("장편영화");
        assertThat(JobCategory.WEB_DRAMA.getDisplayName()).isEqualTo("웹드라마");
        assertThat(JobCategory.ADVERTISEMENT.getDisplayName()).isEqualTo("광고");
        assertThat(JobCategory.MUSIC_VIDEO.getDisplayName()).isEqualTo("뮤직비디오");
        assertThat(JobCategory.OTHER.getDisplayName()).isEqualTo("기타");
    }

    @Test
    @DisplayName("JobStatus enum이 올바른 displayName을 반환한다")
    void jobStatus_displayName() {
        assertThat(JobStatus.RECRUITING.getDisplayName()).isEqualTo("모집중");
        assertThat(JobStatus.CLOSED.getDisplayName()).isEqualTo("마감됨");
    }
}
