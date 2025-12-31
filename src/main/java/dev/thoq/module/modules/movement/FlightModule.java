package dev.thoq.module.modules.movement;

import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.modules.movement.flight.MotionFlightMode;
import dev.thoq.module.modules.movement.flight.StaticFlightMode;
import dev.thoq.module.setting.ModeSetting;
import dev.thoq.module.setting.NumberSetting;

public final class FlightModule extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Flight mode", "Motion", "Motion", "Static");
    private final NumberSetting speed = new NumberSetting("Speed", "How fast", 1D, 0.1, 10.0);

    public FlightModule() {
        super("Flight", "Airplane", Category.MOVEMENT);
        initializeSettings(mode, speed);

        speed.setVisibility(() -> mode.is("Motion"));

        initializeSubmodules(
                new MotionFlightMode(this),
                new StaticFlightMode(this)
        );

        mode.setOnChange(this::updateSubmodules);
    }

    public ModeSetting getMode() {
        return mode;
    }

    public NumberSetting getSpeed() {
        return speed;
    }


}
