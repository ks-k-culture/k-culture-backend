package restapi.kculturebackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT Bearer Token";
        
        // JWT 인증 방식 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList(jwtSchemeName);
        
        Components components = new Components()
            .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT 토큰을 입력하세요")
            );

        return new OpenAPI()
            .info(new Info()
                .title("K-Culture Backend API")
                .description("한국 문화 콘텐츠 관리 시스템 REST API 문서")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("K-Culture Team")
                    .email("contact@kculture.com")
                )
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")
                )
            )
            .components(components)
            .addSecurityItem(securityRequirement);
    }
}

