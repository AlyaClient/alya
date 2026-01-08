package dev.thoq.util.misc;

import dev.thoq.Alya;
import dev.thoq.util.IUtil;
import org.lwjgl.opengl.Display;

public final class Title implements IUtil {

    public static void update(final Class<?> clazz) {
        final String name = clazz.getName();

        switch(name) {
            case "net.minecraft.client.gui.GuiSelectWorld":
                set("Singleplayer");
                break;
            case "net.minecraft.client.gui.GuiMultiplayer":
                set("Multiplayer");
                break;
            case "dev.thoq.gui.auth.AltManagerGui":
                set("Alt Manager");
                break;
            case "net.minecraft.client.Minecraft":
                set("Starting...");
                break;
            default:
                set("");
                break;
        }
    }

    public static void set(final String title) {
        final String base = String.format("%s %s | Minecraft 1.8.9", Alya.getName(), Alya.getVersion());
        if(title.isEmpty()) {
            Display.setTitle(base);
        } else {
            Display.setTitle(String.format("%s - %s", base, title));
        }
    }


}
