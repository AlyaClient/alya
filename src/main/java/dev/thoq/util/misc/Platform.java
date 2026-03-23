package dev.thoq.util.misc;

public class Platform {
  public static final String OS = System.getProperty("os.name").toLowerCase();

  public static void apply() {
    if (OS.contains("mac")) {
      System.setProperty("apple.awt.application.appearance", "system");
      System.setProperty("apple.laf.useScreenMenuBar", "true");
    }
  }
}
