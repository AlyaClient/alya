import dev.thoq.util.misc.Array;
import dev.thoq.util.misc.Platform;
import net.minecraft.client.main.Main;

public class Start {

    public static void main(String[] args) {
        Platform.apply();
        Main.main(
            Array.concat(
                new String[]{
                        "--version", "Alya_1.0",
                        "--accessToken", "0",
                        "--assetsDir", "assets",
                        "--userProperties", "{}"
                }, args
            )
        );
    }


}