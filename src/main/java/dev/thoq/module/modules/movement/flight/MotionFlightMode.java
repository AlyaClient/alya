package dev.thoq.module.modules.movement.flight;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.PlayerMoveEvent;
import dev.thoq.module.Submodule;
import dev.thoq.module.modules.movement.FlightModule;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.util.movement.MovementUtil;

public final class MotionFlightMode extends Submodule {

    private final BooleanSetting glide = new BooleanSetting("Glide", "Slowly fall", false);
    private final BooleanSetting smooth = new BooleanSetting("Smooth", "Smooth movement", true);

    public MotionFlightMode(final FlightModule parent) {
        super("Motion", parent);
        addSetting(glide);
        addSetting(smooth);

        glide.setVisibility(() -> parent.getMode().is("Motion"));
        smooth.setVisibility(() -> parent.getMode().is("Motion"));
    }

    @EventHandler
    public void onPlayerMoveEvent(final PlayerMoveEvent event) {
        if(!smooth.isEnabled()) {
            MC.thePlayer.cameraYaw = 0.1f;
        }

        final FlightModule flightModule = (FlightModule) parent;

        if(MC.gameSettings.keyBindJump.isKeyDown()) {
            MC.thePlayer.motionY += flightModule.getSpeed().getValue() / 2D;
        } else if(MC.gameSettings.keyBindSneak.isKeyDown()) {
            MC.thePlayer.motionY -= flightModule.getSpeed().getValue() / 2D;
        } else {
            MC.thePlayer.motionY = glide.isEnabled() ? -0.05 : 0;
        }

        MovementUtil.setSpeed(flightModule.getSpeed().getValue());
    }


}
