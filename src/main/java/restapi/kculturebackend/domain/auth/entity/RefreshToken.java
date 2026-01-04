package restapi.kculturebackend.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

/**
 * Refresh Token Redis 엔티티
 */
@RedisHash(value = "refreshToken")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String id; // userId

    @Indexed
    private String token;

    private String email;

    @TimeToLive
    private Long expiration; // 초 단위 (7일 = 604800초)

    public static RefreshToken of(String userId, String email, String token, Long expiration) {
        return RefreshToken.builder()
                .id(userId)
                .email(email)
                .token(token)
                .expiration(expiration)
                .build();
    }
}

