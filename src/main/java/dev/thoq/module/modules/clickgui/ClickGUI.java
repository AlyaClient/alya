package dev.thoq.module.modules.clickgui;

import dev.thoq.module.Category;
import dev.thoq.module.Module;
import org.lwjgl.input.Keyboard;

public final class ClickGUI extends Module {
    private final ClickGUIScreen screen;

    public ClickGUI() {
        super(
                "ClickGUI", "Opens the click GUI to manage modules", Category.VISUAL, Keyboard.KEY_RSHIFT);
        this.screen = new ClickGUIScreen();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(MC.theWorld != null) {
            MC.displayGuiScreen(screen);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(MC.currentScreen instanceof ClickGUIScreen) {
            MC.displayGuiScreen(null);
        }
    }

    public ClickGUIScreen getScreen() {
        return screen;
    }
}
