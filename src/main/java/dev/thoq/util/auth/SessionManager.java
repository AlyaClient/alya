package dev.thoq.util.auth;

import dev.thoq.Alya;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

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
    Alya.getInstance()
        .getLogger()
        .info(
            "[SessionManager] setSession called: username={}, uuid={}",
            session.getUsername(),
            session.getPlayerID());
    mc.addScheduledTask(
        () -> {
          try {
            Field field = getSessionField();
            if (field == null) {
              Alya.getInstance()
                  .getLogger()
                  .error("[SessionManager] Session field not found via reflection");
              return;
            }
            Alya.getInstance()
                .getLogger()
                .info("[SessionManager] Reflecting session field: {}", field.getName());
            field.set(mc, session);
            Session verify = mc.getSession();
            Alya.getInstance()
                .getLogger()
                .info(
                    "[SessionManager] Session set, verify: username={}, uuid={}",
                    verify.getUsername(),
                    verify.getPlayerID());
          } catch (Exception e) {
            Alya.getInstance()
                .getLogger()
                .error("[SessionManager] Failed to set session via reflection", e);
          }
        });
  }
}
