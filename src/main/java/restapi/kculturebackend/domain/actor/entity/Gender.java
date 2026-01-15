package restapi.kculturebackend.domain.actor.entity;

/**
 * 성별 Enum
 */
public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromDisplayName(String displayName) {
        for (Gender gender : values()) {
            if (gender.displayName.equals(displayName)) {
                return gender;
            }
        }
        return null;
    }
}
