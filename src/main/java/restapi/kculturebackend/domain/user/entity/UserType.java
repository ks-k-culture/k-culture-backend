package restapi.kculturebackend.domain.user.entity;

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

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}

