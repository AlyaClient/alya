package dev.thoq.module.modules.clickgui;

import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.NumberSetting;
import org.lwjgl.input.Keyboard;

public final class ClickGUI extends Module {
    private final ClickGUIScreen screen;
    public final NumberSetting scale = new NumberSetting("Scale", "Scale of ClickGUI", 1.0F, 0.1, 5.0, 0.05);

    public ClickGUI() {
        super(
                "ClickGUI", "Opens the click gui to manage modules", Category.VISUAL, Keyboard.KEY_RSHIFT);
        initializeSettings(scale);
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
