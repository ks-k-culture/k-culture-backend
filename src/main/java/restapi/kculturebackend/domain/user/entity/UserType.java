package restapi.kculturebackend.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 사용자 유형 (배우/에이전시)
 */
public enum UserType {
    ACTOR("actor", "배우"),
    AGENCY("agency", "에이전시");

    private final String value;
    private final String description;

    UserType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static UserType fromValue(String value) {
        for (UserType type : UserType.values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown UserType: " + value);
    }
}
