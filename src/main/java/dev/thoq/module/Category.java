package dev.thoq.module;

public enum Category {

    COMBAT("Combat"),
    MOVEMENT("Movement"),
    VISUAL("Visual"),
    PLAYER("Player"),
    OTHER("Other");

    private final String displayName;

    Category(final String displayName) {
        this.displayName = displayName;
    }

    public final String getDisplayName() {
        return displayName;
    }

}
