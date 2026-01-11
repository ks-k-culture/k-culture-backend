package restapi.kculturebackend.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import restapi.kculturebackend.config.TestContainersConfig;
import restapi.kculturebackend.domain.job.entity.Job;
import restapi.kculturebackend.domain.job.entity.JobCategory;
import restapi.kculturebackend.domain.job.entity.JobStatus;
import restapi.kculturebackend.domain.job.repository.JobRepository;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.repository.UserRepository;
import restapi.kculturebackend.fixture.TestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JobRepository 단위 테스트
 * - searchJobs 메서드의 null 파라미터 처리 검증
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@Transactional
@DisplayName("JobRepository 테스트")
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 정리
        jobRepository.deleteAll();

        // 테스트 사용자 생성
        testUser = userRepository.save(TestFixtures.createActorUserWithRandomEmail());
        defaultPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Nested
    @DisplayName("searchJobs 메서드")
    class SearchJobsTest {

        @Test
        @DisplayName("모든 파라미터가 null일 때 정상 동작해야 한다")
        void searchJobs_withAllNullParams_shouldWork() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "단편영화 배우 모집", "청춘시대"));
            jobRepository.save(TestFixtures.createJob(testUser, "광고 모델 모집", "브랜드 광고"));

            // when
            Page<Job> result = jobRepository.searchJobs(null, null, null, null, null, defaultPageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("search 파라미터가 null일 때 정상 동작해야 한다 (핵심 버그 수정 테스트)")
        void searchJobs_withNullSearch_shouldNotThrowByteaError() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "테스트 공고", "테스트 작품"));

            // when - search가 null일 때 bytea 오류가 발생하면 안 됨
            Page<Job> result = jobRepository.searchJobs(
                    JobStatus.RECRUITING,  // status
                    JobCategory.SHORT_FILM, // category
                    "남자",                  // gender
                    false,                   // isPumasi
                    null,                    // search (핵심: null 전달)
                    defaultPageable
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("search 파라미터가 빈 문자열일 때 정상 동작해야 한다")
        void searchJobs_withEmptySearch_shouldWork() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "테스트 공고", "테스트 작품"));

            // when
            Page<Job> result = jobRepository.searchJobs(null, null, null, null, "", defaultPageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("search 파라미터로 title 검색이 동작해야 한다")
        void searchJobs_withSearchByTitle_shouldFindMatching() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "영화 배우 급구", "작품A"));
            jobRepository.save(TestFixtures.createJob(testUser, "드라마 모델 모집", "작품B"));
            jobRepository.save(TestFixtures.createJob(testUser, "광고 촬영 배우", "작품C"));

            // when
            Page<Job> result = jobRepository.searchJobs(null, null, null, null, "배우", defaultPageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(Job::getTitle)
                    .allMatch(title -> title.contains("배우"));
        }

        @Test
        @DisplayName("search 파라미터로 workTitle 검색이 동작해야 한다")
        void searchJobs_withSearchByWorkTitle_shouldFindMatching() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "배우 모집1", "청춘시대"));
            jobRepository.save(TestFixtures.createJob(testUser, "배우 모집2", "사랑의 불시착"));
            jobRepository.save(TestFixtures.createJob(testUser, "배우 모집3", "청춘로맨스"));

            // when
            Page<Job> result = jobRepository.searchJobs(null, null, null, null, "청춘", defaultPageable);

            // then
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("search가 대소문자 구분 없이 동작해야 한다")
        void searchJobs_shouldBeCaseInsensitive() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "Movie Actor Wanted", "Test Film"));

            // when
            Page<Job> resultLower = jobRepository.searchJobs(null, null, null, null, "movie", defaultPageable);
            Page<Job> resultUpper = jobRepository.searchJobs(null, null, null, null, "MOVIE", defaultPageable);
            Page<Job> resultMixed = jobRepository.searchJobs(null, null, null, null, "MoViE", defaultPageable);

            // then
            assertThat(resultLower.getContent()).hasSize(1);
            assertThat(resultUpper.getContent()).hasSize(1);
            assertThat(resultMixed.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("status 필터가 정상 동작해야 한다")
        void searchJobs_withStatusFilter_shouldWork() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "모집중 공고", "작품A"));
            Job closedJob = TestFixtures.createJob(testUser, "마감된 공고", "작품B");
            closedJob.close();
            jobRepository.save(closedJob);

            // when
            Page<Job> recruitingResult = jobRepository.searchJobs(JobStatus.RECRUITING, null, null, null, null, defaultPageable);
            Page<Job> closedResult = jobRepository.searchJobs(JobStatus.CLOSED, null, null, null, null, defaultPageable);

            // then
            assertThat(recruitingResult.getContent()).hasSize(1);
            assertThat(recruitingResult.getContent().get(0).getTitle()).isEqualTo("모집중 공고");

            assertThat(closedResult.getContent()).hasSize(1);
            assertThat(closedResult.getContent().get(0).getTitle()).isEqualTo("마감된 공고");
        }

        @Test
        @DisplayName("category 필터가 정상 동작해야 한다")
        void searchJobs_withCategoryFilter_shouldWork() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "단편영화 공고", JobCategory.SHORT_FILM));
            jobRepository.save(TestFixtures.createJob(testUser, "광고 공고", JobCategory.ADVERTISEMENT));

            // when
            Page<Job> shortFilmResult = jobRepository.searchJobs(null, JobCategory.SHORT_FILM, null, null, null, defaultPageable);
            Page<Job> adResult = jobRepository.searchJobs(null, JobCategory.ADVERTISEMENT, null, null, null, defaultPageable);

            // then
            assertThat(shortFilmResult.getContent()).hasSize(1);
            assertThat(shortFilmResult.getContent().get(0).getCategory()).isEqualTo(JobCategory.SHORT_FILM);

            assertThat(adResult.getContent()).hasSize(1);
            assertThat(adResult.getContent().get(0).getCategory()).isEqualTo(JobCategory.ADVERTISEMENT);
        }

        @Test
        @DisplayName("복합 필터가 정상 동작해야 한다")
        void searchJobs_withMultipleFilters_shouldWork() {
            // given
            jobRepository.save(TestFixtures.createJob(testUser, "단편영화 배우", JobCategory.SHORT_FILM));
            jobRepository.save(TestFixtures.createJob(testUser, "광고 모델", JobCategory.ADVERTISEMENT));
            jobRepository.save(TestFixtures.createJob(testUser, "단편영화 스태프", JobCategory.SHORT_FILM));

            // when - 단편영화 + "배우" 검색
            Page<Job> result = jobRepository.searchJobs(
                    null,
                    JobCategory.SHORT_FILM,
                    null,
                    null,
                    "배우",
                    defaultPageable
            );

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("단편영화 배우");
        }
    }
}
