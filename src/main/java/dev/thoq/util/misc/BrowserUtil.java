package dev.thoq.util.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrowserUtil {
  private static final Logger logger = LogManager.getLogger(BrowserUtil.class);

  public static void open(String url) {
    String os = Platform.OS;
    try {
      if (os.contains("linux") || os.contains("unix")) {
        Runtime.getRuntime().exec(new String[] {"xdg-open", url});
      } else if (os.contains("mac")) {
        Runtime.getRuntime().exec(new String[] {"open", url});
      } else if (os.contains("win")) {
        Runtime.getRuntime().exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", url});
      } else {
        logger.error("Unsupported platform for opening URLs: {}", os);
      }
    } catch (Exception e) {
      logger.error("Couldn't open link: {}", url, e);
    }
  }
}
