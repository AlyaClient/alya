package dev.thoq.module.modules.movement.speed;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.MotionEvent;
import dev.thoq.module.Submodule;
import dev.thoq.module.modules.movement.SpeedModule;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.util.movement.MovementUtil;

public final class BHopSpeedMode extends Submodule {

    private final BooleanSetting strafe = new BooleanSetting("Strafe", "Strafe while jumping", true);
    private final BooleanSetting omniSprint = new BooleanSetting("OmniSprint", "Sprint constantly in all drections", true);

    public BHopSpeedMode(final SpeedModule parent) {
        super("BHop", parent);

        addSetting(strafe);
        addSetting(omniSprint);
        strafe.setVisibility(() -> parent.getMode().is("BHop"));
        omniSprint.setVisibility(() -> parent.getMode().is("BHop"));
    }

    @EventHandler
    private void onMotion(final MotionEvent event) {
        if(!MovementUtil.isMoving()) {
            return;
        }

        if(MC.thePlayer.onGround) {
            MC.thePlayer.jump();
        }

        final int strafePercentage = strafe.isEnabled() ? 1 : 0;
        if(MC.gameSettings.keyBindSprint.isKeyDown() || MC.thePlayer.isSprinting() || omniSprint.isEnabled()) {
            MC.thePlayer.setSprinting(true);
            MovementUtil.setSpeed(MovementUtil.SPRINT_SPEED, strafePercentage);
        } else {
            MovementUtil.setSpeed(MovementUtil.WALK_SPEED, strafePercentage);
        }
    }


}
