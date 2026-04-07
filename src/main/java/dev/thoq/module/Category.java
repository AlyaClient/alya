package dev.thoq.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    EXPLOIT("Exploit"),
    OTHER("Other"),
    VISUAL("Visual"),
    SCRIPTS("Scripts");
    private final String displayName;

    Category(final String displayName) {
        this.displayName = displayName;
    }

    public final String getDisplayName() {
        return displayName;
    }
}
