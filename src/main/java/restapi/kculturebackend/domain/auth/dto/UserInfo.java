package restapi.kculturebackend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import restapi.kculturebackend.domain.user.entity.User;
import restapi.kculturebackend.domain.user.entity.UserType;

import java.util.UUID;

/**
 * 사용자 정보 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private UUID id;
    private String email;
    private String name;
    private UserType type;
    private String profileImage;

    public static UserInfo from(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .type(user.getType())
                .profileImage(user.getProfileImage())
                .build();
    }
}

