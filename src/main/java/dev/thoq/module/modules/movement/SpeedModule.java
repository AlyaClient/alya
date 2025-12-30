package dev.thoq.module.modules.movement;

import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.modules.movement.speed.BHopSpeedMode;
import dev.thoq.module.modules.movement.speed.VanillaSpeedMode;
import dev.thoq.module.setting.ModeSetting;

public final class SpeedModule extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Speed mode", "BHop", "BHop", "Vanilla");

    public SpeedModule() {
        super("Speed", "F1 car", Category.MOVEMENT);

        addSetting(mode);
        addSubmodule(new BHopSpeedMode(this));
        addSubmodule(new VanillaSpeedMode(this));
    }

    public ModeSetting getMode() {
        return mode;
    }


}
