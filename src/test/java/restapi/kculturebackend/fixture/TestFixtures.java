package restapi.kculturebackend.fixture;

import java.util.UUID;

import restapi.kculturebackend.domain.job.entity.Job;
import restapi.kculturebackend.domain.job.entity.JobCategory;
import restapi.kculturebackend.domain.job.entity.JobStatus;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

/**
 * 테스트용 Fixture 데이터
 */
public class TestFixtures {

    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "password123";
    public static final String TEST_NAME = "테스트유저";

    /**
     * 테스트용 배우 User 생성
     */
    public static User createActorUser() {
        return User.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD) // 실제로는 인코딩 필요
                .name(TEST_NAME)
                .type(UserType.ACTOR)
                .isActive(true)
                .build();
    }

    /**
     * 테스트용 에이전시 User 생성
     */
    public static User createAgencyUser() {
        return User.builder()
                .email("agency@example.com")
                .password(TEST_PASSWORD)
                .name("테스트에이전시")
                .type(UserType.AGENCY)
                .isActive(true)
                .build();
    }

    /**
     * 랜덤 이메일로 배우 User 생성
     */
    public static User createActorUserWithRandomEmail() {
        return User.builder()
                .email("actor-" + UUID.randomUUID() + "@example.com")
                .password(TEST_PASSWORD)
                .name(TEST_NAME)
                .type(UserType.ACTOR)
                .isActive(true)
                .build();
    }

    /**
     * 테스트용 Job 생성
     */
    public static Job createJob(User user, String title, String workTitle) {
        return Job.builder()
                .user(user)
                .category(JobCategory.SHORT_FILM)
                .isPumasi(false)
                .price(100000)
                .title(title)
                .description("테스트 설명")
                .gender("남자")
                .ageRange("20대")
                .production("테스트 프로덕션")
                .workTitle(workTitle)
                .shootingDate("2026-02-01")
                .shootingLocation("서울")
                .status(JobStatus.RECRUITING)
                .contactEmail("contact@test.com")
                .contactPhone("010-1234-5678")
                .build();
    }

    /**
     * 테스트용 Job 생성 (카테고리 지정)
     */
    public static Job createJob(User user, String title, JobCategory category) {
        return Job.builder()
                .user(user)
                .category(category)
                .isPumasi(false)
                .price(100000)
                .title(title)
                .description("테스트 설명")
                .gender("남자")
                .ageRange("20대")
                .production("테스트 프로덕션")
                .workTitle("테스트 작품")
                .shootingDate("2026-02-01")
                .shootingLocation("서울")
                .status(JobStatus.RECRUITING)
                .contactEmail("contact@test.com")
                .contactPhone("010-1234-5678")
                .build();
    }
}

