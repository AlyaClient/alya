import net.minecraft.client.main.Main;

void main(String[] args) {
    System.setProperty("apple.awt.application.appearance", "system");
    System.setProperty("apple.laf.useScreenMenuBar", "true");

    Main.main(concat(new String[]{"--version", "1.0", "--accessToken", "0", "--assetsDir", "assets", "--userProperties", "{}"}, args));
}

public static <T> T[] concat(T[] first, T[] second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
}
