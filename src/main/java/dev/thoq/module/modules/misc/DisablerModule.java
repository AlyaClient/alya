package dev.thoq.module.modules.misc;

import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.modules.misc.disabler.OmniSprint;
import dev.thoq.module.setting.ModeSetting;

public final class DisablerModule extends Module {

    private ModeSetting mode = new ModeSetting("Mode", "Disabler Mode", "OmniSprint", "OmniSprint");

    public DisablerModule() {
        super("Disabler", "Attempts to disable certain checks on anticheats", Category.MISC);

        initializeSettings(mode);
        initializeSubmodules(new OmniSprint(this));
    }


}
