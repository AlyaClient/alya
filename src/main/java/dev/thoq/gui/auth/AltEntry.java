package dev.thoq.gui.auth;

public final class AltEntry {

    public enum Type {
        CRACKED, MICROSOFT
    }

    private final String name;
    private final String uuid;
    private final String accessToken;
    private final Type type;
    private boolean favorite;

    public AltEntry(String name, String uuid, String accessToken, Type type, boolean favorite) {
        this.name = name;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.type = type;
        this.favorite = favorite;
    }

    public static AltEntry cracked(String name) {
        return new AltEntry(name, name, "0", Type.CRACKED, false);
    }

    public static AltEntry microsoft(String name, String uuid, String accessToken) {
        return new AltEntry(name, uuid, accessToken, Type.MICROSOFT, false);
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Type getType() {
        return type;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getSkinUrl() {
        if(type == Type.MICROSOFT && uuid != null && !uuid.isEmpty()) {
            return "https://minotar.net/helm/" + uuid + "/128.png";
        }
        return null;
    }


}
