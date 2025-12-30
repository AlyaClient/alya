package dev.thoq.util.auth;

import dev.thoq.Alya;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;

public class SessionManager {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Field sessionField = null;

    public static Field getSessionField() {
        if(sessionField == null) {
            try {
                for(final Field field : Minecraft.class.getDeclaredFields()) {
                    if(field.getType().isAssignableFrom(Session.class)) {
                        sessionField = field;
                        sessionField.setAccessible(true);
                        break;
                    }
                }
            } catch(Exception exception) {
                Alya.getInstance().getLogger().error("Failed to get session field", exception);
                sessionField = null;
            }
        }
        return sessionField;
    }

    public static Session getSession() {
        return mc.getSession();
    }

    public static void setSession(final Session session) {
        try {
            getSessionField().set(mc, session);
        } catch(IllegalAccessException illegalAccessException) {
            Alya.getInstance().getLogger().error("Failed to set session", illegalAccessException);
        }
    }


}
