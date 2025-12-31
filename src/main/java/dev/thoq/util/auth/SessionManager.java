package dev.thoq.util.auth;

import dev.thoq.Alya;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;

public class SessionManager {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Field sessionField;

    private static Field getSessionField() {
        if (sessionField == null) {
            for (Field field : Minecraft.class.getDeclaredFields()) {
                if (field.getType() == Session.class) {
                    field.setAccessible(true);
                    sessionField = field;
                    break;
                }
            }
        }
        return sessionField;
    }

    public static Session getSession() {
        return mc.getSession();
    }

    public static void setSession(final Session session) {
        mc.addScheduledTask(() -> {
            try {
                Field field = getSessionField();
                if (field == null) {
                    Alya.getInstance().getLogger().error("Session field not found");
                    return;
                }
                field.set(mc, session);
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("Failed to set session", e);
            }
        });
    }


}
