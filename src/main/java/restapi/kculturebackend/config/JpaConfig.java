package restapi.kculturebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정 - Auditing 활성화
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}

