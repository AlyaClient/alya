package start;

import dev.thoq.util.misc.Array;
import dev.thoq.util.misc.Platform;

public final class Main {

    public static void main(final String[] args) {
        Platform.apply();
        final String[] combined = Array.concat(new String[]{
                "--version", "1.8.9",
                "--accessToken", "0",
                "--assetsDir", "assets",
                "--userProperties", "{}"
        }, args);
        net.minecraft.client.main.Main.main(combined);
    }


}
