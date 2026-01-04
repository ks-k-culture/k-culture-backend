package restapi.kculturebackend.fixture;

import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.UUID;

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
}

