package dev.thoq.util.misc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public final class Platform {

    public static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String ICON_PATH = "/assets/minecraft/Alya/Icons/32x32.png";
    private static final String ABOUT = """
            Alya Client is a free, open-source Minecraft hacked client.

            (c) 2025-2026 Thoq
            GPL-2 License
            """;

    public static void apply() {
        if(!OS.contains("mac")) return;

        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Alya");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Alya");

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
                "About Alya",
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
