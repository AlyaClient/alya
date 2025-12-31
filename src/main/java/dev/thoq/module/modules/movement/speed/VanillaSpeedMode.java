package dev.thoq.module.modules.movement.speed;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.MotionEvent;
import dev.thoq.module.Submodule;
import dev.thoq.module.modules.movement.SpeedModule;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.module.setting.NumberSetting;
import dev.thoq.util.movement.MovementUtil;

public final class VanillaSpeedMode extends Submodule {

    private final NumberSetting speed = new NumberSetting("Speed", "How fast", 0.6, 0.1, 10.0);
    private final BooleanSetting autoJump = new BooleanSetting("Auto Jump", "Automatically jump", true);
    private final BooleanSetting strafe = new BooleanSetting("Strafe", "Move mid-air", true);

    public VanillaSpeedMode(final SpeedModule parent) {
        super("Vanilla", parent);
        initializeSettings(speed, autoJump, strafe);

        speed.setVisibility(() -> parent.getMode().is("Vanilla"));
        autoJump.setVisibility(() -> parent.getMode().is("Vanilla"));
        strafe.setVisibility(() -> parent.getMode().is("Vanilla"));
    }

    @EventHandler
    private void onMotionEvent(final MotionEvent event) {
        if(!MovementUtil.isMoving()) {
            return;
        }

        if(autoJump.isEnabled() && MC.thePlayer.onGround) {
            MC.thePlayer.jump();
        }

        final int strafePercentage = strafe.isEnabled() ? 1 : 0;
        MovementUtil.setSpeed(speed.getValue(), strafePercentage);
    }


}
