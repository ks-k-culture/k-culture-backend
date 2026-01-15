package restapi.kculturebackend.domain.actor.entity;

/**
 * 배우 구분 Enum (배우/모델)
 */
public enum ActorCategory {
    ACTOR("배우"),
    MODEL("모델");

    private final String displayName;

    ActorCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ActorCategory fromDisplayName(String displayName) {
        for (ActorCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        return null;
    }
}
