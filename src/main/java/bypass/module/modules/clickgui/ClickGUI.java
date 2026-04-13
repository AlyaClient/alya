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

package bypass.module.modules.clickgui;

import bypass.module.Category;
import bypass.module.Module;
import bypass.module.setting.NumberSetting;
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
