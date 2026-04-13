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

package bypass.util.misc;

import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public final class Platform {

    public static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String ICON_PATH = "/assets/minecraft/client/icons/32x32.png";
    private static final String ABOUT = """
            client Client is a free, open-source Minecraft hacked client.

            (c) 2025-2026 Thoq
            GPL-2 License
            """;

    public static void apply() {
        if(OS.contains("linux")) {
            GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_X11);
        }

        if(!OS.contains("mac")) return;

        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "client");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "client");

        setDockIcon();
        setAboutHandler();
    }

    private static void setDockIcon() {
        if(!Taskbar.isTaskbarSupported()) {
            return;
        }

        try {
            final BufferedImage icon = loadIcon();
            if(icon != null) {
                Taskbar.getTaskbar().setIconImage(icon);
            }
        } catch(final Exception ignored) {
        }
    }

    private static void setAboutHandler() {
        if(!Desktop.isDesktopSupported()) {
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if(!desktop.isSupported(Desktop.Action.APP_ABOUT)) {
            return;
        }

        desktop.setAboutHandler(aboutEvent -> showAboutDialog());
    }

    private static void showAboutDialog() {
        Icon icon = null;
        final BufferedImage img = loadIcon();
        if(img != null) {
            icon = new ImageIcon(img);
        }

        JOptionPane.showMessageDialog(
                null,
                ABOUT,
                "About client",
                JOptionPane.INFORMATION_MESSAGE,
                icon
        );
    }

    private static BufferedImage loadIcon() {
        try(final InputStream stream = Platform.class.getResourceAsStream(ICON_PATH)) {
            if(stream == null) {
                return null;
            }
            return ImageIO.read(stream);
        } catch(final Exception exception) {
            return null;
        }
    }


}
