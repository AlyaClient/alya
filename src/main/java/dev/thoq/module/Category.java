package dev.thoq.module;

public enum Category {

    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player"),
    WORLD("World"),
    MISC("Misc");

    private final String displayName;

    Category(final String displayName) {
        this.displayName = displayName;
    }

    public final String getDisplayName() {
        return displayName;
    }


}
