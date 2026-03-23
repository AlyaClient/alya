package start;

import dev.thoq.util.misc.Platform;
import net.minecraft.client.main.McMain;

public class Main {

  public static void main(String[] args) {
    Platform.apply();
    String[] defaults =
        new String[] {
          "--version", "1.8.9",
          "--accessToken", "0",
          "--assetsDir", "assets",
          "--userProperties", "{}"
        };
    String[] combined = new String[defaults.length + args.length];
    System.arraycopy(defaults, 0, combined, 0, defaults.length);
    System.arraycopy(args, 0, combined, defaults.length, args.length);
    McMain.mcmain(combined);
  }
}
