/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package bypass.util.auth;

import bypass.Alya;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;

public class SessionManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Field sessionField;

    private static Field getSessionField() {
        if(sessionField == null) {
            for(Field field : Minecraft.class.getDeclaredFields()) {
                if(field.getType() == Session.class) {
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
                        if(field == null) {
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
                    } catch(Exception e) {
                        Alya.getInstance()
                                .getLogger()
                                .error("[SessionManager] Failed to set session via reflection", e);
                    }
                });
    }
}
